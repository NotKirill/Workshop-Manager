package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.author.AuthorService;
import notkirill.workshopmanager.game.GameResolutionService;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.SourceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class SteamModspaceAdapter implements ModspaceAdapter {
    private static final Logger log = LoggerFactory.getLogger(SteamModspaceAdapter.class);

    private static final Pattern ORIGINAL_ID = Pattern.compile("\\d{1,20}");

    private final SteamWorkshopClient workshopClient;
    private final SteamUserClient userClient;
    private final GameResolutionService gameResolutionService;
    private final AuthorService authorService;
    private final SteamItemReaderService itemReaderService;
    private final SteamItemWriterService itemWriterService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public SteamModspaceAdapter(SteamWorkshopClient workshopClient, SteamUserClient userClient,
                                GameResolutionService gameResolutionService,
                                AuthorService authorService, SteamItemReaderService itemReaderService,
                                SteamItemWriterService itemWriterService,
                                PlatformTransactionManager transactionManager) {
        this.workshopClient = workshopClient;
        this.userClient = userClient;
        this.gameResolutionService = gameResolutionService;
        this.authorService = authorService;
        this.itemReaderService = itemReaderService;
        this.itemWriterService = itemWriterService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public ModspaceType type() {
        return ModspaceType.STEAM;
    }

    @Override
    public boolean isValidOriginalId(String originalId) {
        return ORIGINAL_ID.matcher(originalId).matches();
    }

    @Override
    public <R> List<R> sync(Collection<String> originalIds, Function<SourceItem, R> store) {
        List<String> ids = new ArrayList<>(new LinkedHashSet<>(originalIds));
        List<SteamPublishedFile> files = workshopClient.getPublishedFileDetails(ids);
        Map<String, String> authors = fetchNewAuthorNames(files);

        List<R> result = new ArrayList<>();
        for (SteamPublishedFile file : files) {
            result.add(persistWithRetry(file, authors, store));
        }
        return result;
    }

    @Override
    public List<String> getDependencies(String originalId) {
        return itemReaderService.getDependencyIds(originalId);
    }

    @Override
    public void deleteModspaceData(String originalId) {
        itemWriterService.delete(originalId);
    }

    @Override
    public boolean supportsAuthorNames() {
        return userClient.supportsPlayerSummaries();
    }

    @Override
    public Map<String, String> fetchAuthorNames(Collection<String> originalIds) {
        Map<String, String> names = new HashMap<>();
        userClient.getPlayerSummaries(originalIds).forEach((steamId, summary) -> {
            if (summary.personaName() != null) {
                names.put(steamId, summary.personaName());
            }
        });
        return names;
    }

    private <R> R persistWithRetry(SteamPublishedFile file, Map<String, String> authors,
                                   Function<SourceItem, R> store) {
        try {
            return transactionTemplate.execute(status -> store.apply(persist(file, authors)));
        } catch (DataIntegrityViolationException e) {
            log.debug("Retrying Steam item {} after concurrent write: {}", file.publishedFileId(), e.getMessage());
            return transactionTemplate.execute(status -> store.apply(persist(file, authors)));
        }
    }

    private SourceItem persist(SteamPublishedFile file, Map<String, String> authors) {
        String id = file.publishedFileId();
        if (!file.isAvailable()) {
            return SourceItem.unavailable(ModspaceType.STEAM, id);
        }

        long gameId = gameResolutionService.resolve(
                ModspaceType.STEAM, String.valueOf(file.consumerAppId()),
                file.details() != null ? file.details().appName() : null, "Steam App " + file.consumerAppId()
        );
        SteamItemWriterService.SaveResult saved = itemWriterService.save(file);

        Long authorId = null;
        if (file.creator() != null) {
            authorId = authorService.recordSeen(
                    ModspaceType.STEAM, file.creator(), authors.get(file.creator()),
                    supportsAuthorNames()
            );
        }

        return new SourceItem(
                ModspaceType.STEAM, id, true, gameId, file.title(), file.description(), authorId,
                file.fileSize(), file.timeCreated(), file.timeUpdated(), saved.dependenciesChanged(),
                saved.mediaChanged()
        );
    }

    private Map<String, String> fetchNewAuthorNames(List<SteamPublishedFile> files) {
        if (!supportsAuthorNames()) {
            return Map.of();
        }
        Set<String> creators = new HashSet<>();
        for (SteamPublishedFile file : files) {
            if (file.isAvailable() && file.creator() != null) {
                creators.add(file.creator());
            }
        }
        if (creators.isEmpty()) {
            return Map.of();
        }
        Set<String> unknown = authorService.findUnknown(ModspaceType.STEAM, creators);
        if (unknown.isEmpty()) {
            return Map.of();
        }
        try {
            return fetchAuthorNames(unknown);
        } catch (RuntimeException e) {
            log.warn("Could not fetch new Steam authors: {}", e.getMessage());
            return Map.of();
        }
    }
}
