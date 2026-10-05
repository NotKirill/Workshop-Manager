package notkirill.workshopmanager.author;

import notkirill.workshopmanager.author.refresh.AuthorRefreshProperties;
import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorMetadataRepository repository;
    private final DatabaseLocks databaseLocks;
    private final AuthorRefreshProperties refreshProperties;
    private final Clock clock;

    @Autowired
    public AuthorService(AuthorMetadataRepository repository, DatabaseLocks databaseLocks,
                         AuthorRefreshProperties refreshProperties, Clock clock) {
        this.repository = repository;
        this.databaseLocks = databaseLocks;
        this.refreshProperties = refreshProperties;
        this.clock = clock;
    }

    public Optional<AuthorMetadata> find(long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Set<String> findUnknown(ModspaceType modspace, Collection<String> originalIds) {
        Set<String> known = repository.findBySourceModspaceAndOriginalIdIn(modspace, originalIds).stream()
                .map(AuthorMetadata::getOriginalId)
                .collect(Collectors.toSet());
        return originalIds.stream().filter(id -> !known.contains(id)).collect(Collectors.toSet());
    }

    @Transactional
    public long recordSeen(ModspaceType modspace, String originalId, String fetchedName, boolean refreshable) {
        Optional<AuthorMetadata> existing = repository.findBySourceModspaceAndOriginalId(modspace, originalId);
        if (existing.isEmpty()) {
            databaseLocks.lock("author:" + modspace + ":" + originalId);
            existing = repository.findBySourceModspaceAndOriginalId(modspace, originalId);
        }
        AuthorMetadata author = existing.orElseGet(() -> new AuthorMetadata(modspace, originalId, originalId));
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (fetchedName != null) {
            applyProfile(author, fetchedName, now);
        } else if (refreshable && author.getRefreshRequestedAt() == null && isStale(author, now)) {
            author.setRefreshRequestedAt(now);
        }
        return repository.save(author).getId();
    }

    public void applyProfile(AuthorMetadata author, String fetchedName, OffsetDateTime now) {
        if (fetchedName != null && !fetchedName.isBlank()) {
            author.setName(fetchedName);
        }
        author.setTimeSynced(now);
        author.setRefreshRequestedAt(null);
    }

    private boolean isStale(AuthorMetadata author, OffsetDateTime now) {
        return author.getTimeSynced() == null
                || author.getTimeSynced().isBefore(now.minus(refreshProperties.staleAfter()));
    }
}
