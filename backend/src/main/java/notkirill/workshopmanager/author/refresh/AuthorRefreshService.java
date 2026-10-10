package notkirill.workshopmanager.author.refresh;

import notkirill.workshopmanager.author.AuthorMetadata;
import notkirill.workshopmanager.author.AuthorMetadataRepository;
import notkirill.workshopmanager.author.AuthorService;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorRefreshService {
    private static final Logger log = LoggerFactory.getLogger(AuthorRefreshService.class);

    private final AuthorMetadataRepository repository;
    private final AuthorService authorService;
    private final ModspaceRegistry modspaceRegistry;
    private final AuthorRefreshProperties properties;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    @Autowired
    public AuthorRefreshService(AuthorMetadataRepository repository, AuthorService authorService,
                                ModspaceRegistry modspaceRegistry, AuthorRefreshProperties properties,
                                PlatformTransactionManager transactionManager, Clock clock) {
        this.repository = repository;
        this.authorService = authorService;
        this.modspaceRegistry = modspaceRegistry;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    public RefreshReport refreshQueued() {
        List<AuthorMetadata> queued = repository.findByRefreshRequestedAtIsNotNullOrderByRefreshRequestedAtAsc(
                PageRequest.of(0, properties.batchSize()));
        Map<ModspaceType, List<AuthorMetadata>> byModspace = queued.stream()
                .collect(Collectors.groupingBy(AuthorMetadata::getSourceModspace));

        int refreshed = 0;
        int failed = 0;
        for (Map.Entry<ModspaceType, List<AuthorMetadata>> entry : byModspace.entrySet()) {
            Optional<ModspaceAdapter> adapter = modspaceRegistry.find(entry.getKey())
                    .filter(ModspaceAdapter::supportsAuthorNames);
            if (adapter.isEmpty()) {
                // The Steam API key is not available: the marks stay until names can be fetched again
                continue;
            }
            List<AuthorMetadata> authors = entry.getValue();
            Map<String, String> names;
            try {
                names = adapter.get().fetchAuthorNames(authors.stream().map(AuthorMetadata::getOriginalId).toList());
            } catch (RuntimeException e) {
                // Quota exhausted or modspace down
                log.warn("Author refresh for {} failed: {}", entry.getKey(), e.getMessage());
                failed += authors.size();
                continue;
            }
            transactionTemplate.executeWithoutResult(status -> {
                OffsetDateTime now = OffsetDateTime.now(clock);
                for (AuthorMetadata queuedAuthor : authors) {
                    repository.findById(queuedAuthor.getId()).ifPresent(author -> {
                        // Not returned (e.g. deleted account): marked as synced anyway, so it is not retried forever
                        authorService.applyProfile(author, names.get(author.getOriginalId()), now);
                        repository.save(author);
                    });
                }
            });
            refreshed += authors.size();
        }
        return new RefreshReport(refreshed, failed);
    }

    public long countQueued() {
        return repository.countByRefreshRequestedAtIsNotNull();
    }

    public record RefreshReport(int refreshed, int failed) {
    }
}
