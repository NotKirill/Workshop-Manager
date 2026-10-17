package notkirill.workshopmanager.item.sync;

import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class ItemSyncService {
    private static final Logger log = LoggerFactory.getLogger(ItemSyncService.class);

    private final ItemInternalService internalService;
    private final PersonalItemService personalService;
    private final ModspaceRegistry modspaceRegistry;
    private final SourceFetchService sourceFetchService;
    private final ContentJobService contentJobService;
    private final ItemSyncProperties properties;
    private final Clock clock;

    @Autowired
    public ItemSyncService(ItemInternalService internalService, PersonalItemService personalService,
                           ModspaceRegistry modspaceRegistry, SourceFetchService sourceFetchService,
                           ContentJobService contentJobService, ItemSyncProperties properties, Clock clock) {
        this.internalService = internalService;
        this.personalService = personalService;
        this.modspaceRegistry = modspaceRegistry;
        this.sourceFetchService = sourceFetchService;
        this.contentJobService = contentJobService;
        this.properties = properties;
        this.clock = clock;
    }

    public SyncReport syncStale() {
        int synced = 0;
        int unavailable = 0;
        int backups = 0;
        for (ModspaceAdapter adapter : modspaceRegistry.all()) {
            if (adapter.type() == ModspaceType.LOCAL) {
                continue;
            }
            List<String> ids = internalService.findSyncCandidates(
                            adapter.type(), OffsetDateTime.now(clock).minus(properties.staleAfter()),
                            properties.batchSize()
                    ).stream()
                    .map(ItemMetadataInternal::getOriginalId)
                    .toList();
            if (ids.isEmpty()) {
                continue;
            }
            try {
                for (SyncOutcome outcome : sync(adapter.type(), ids)) {
                    if (outcome.status() == SyncStatus.UNAVAILABLE) {
                        unavailable++;
                    } else {
                        synced++;
                    }
                    if (outcome.sourceChanged()) {
                        backups++;
                    }
                }
            } catch (RuntimeException e) {
                log.warn("Scheduled sync with {} failed: {}", adapter.type(), e.getMessage());
            }
        }
        return new SyncReport(synced, unavailable, backups);
    }

    public List<SyncOutcome> sync(ModspaceType modspace, Collection<String> originalIds) {
        List<SyncOutcome> outcomes = sourceFetchService.sync(modspace, originalIds);
        for (SyncOutcome outcome : outcomes) {
            if (outcome.sourceChanged()) {
                if (personalService.hasAutoBackupUsers(outcome.internalId())) {
                    contentJobService.requestBackup(outcome.internalId(), null);
                }
            }
            if (outcome.dependenciesChanged()) {
                personalService.requestDependencyReview(outcome.internalId());
            }
        }
        return outcomes;
    }

    public record SyncReport(int synced, int unavailable, int backupsQueued) {
    }
}
