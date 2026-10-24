package notkirill.workshopmanager.item.lifecycle;

import notkirill.workshopmanager.common.Texts;
import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.content.ContentRevisionService;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataInternalRepository;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.source.MediaSourceSyncService;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemCleanupService {
    private static final Logger log = LoggerFactory.getLogger(ItemCleanupService.class);

    private final ItemMetadataInternalRepository internalRepository;
    private final ItemCleanupRepository cleanupRepository;
    private final ItemLifecycleService lifecycleService;
    private final ItemProjectionService projectionService;
    private final ContentRevisionService contentRevisionService;
    private final ContentJobService contentJobService;
    private final MediaSourceSyncService mediaSourceSyncService;
    private final ModspaceRegistry modspaceRegistry;
    private final ItemCleanupProperties properties;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    @Autowired
    public ItemCleanupService(ItemMetadataInternalRepository internalRepository,
                              ItemCleanupRepository cleanupRepository,
                              ItemLifecycleService lifecycleService, ItemProjectionService projectionService,
                              ContentRevisionService contentRevisionService, ContentJobService contentJobService,
                              MediaSourceSyncService mediaSourceSyncService,
                              ModspaceRegistry modspaceRegistry, ItemCleanupProperties properties,
                              PlatformTransactionManager transactionManager, Clock clock) {
        this.internalRepository = internalRepository;
        this.cleanupRepository = cleanupRepository;
        this.lifecycleService = lifecycleService;
        this.projectionService = projectionService;
        this.contentRevisionService = contentRevisionService;
        this.contentJobService = contentJobService;
        this.mediaSourceSyncService = mediaSourceSyncService;
        this.modspaceRegistry = modspaceRegistry;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    public CleanupReport runCleanup() {
        int marked = markUnreferenced();
        List<Long> claimed = claimForDeletion();
        int deleted = 0;
        int failed = 0;
        for (Long internalId : claimed) {
            try {
                deleteItem(internalId);
                deleted++;
            } catch (RuntimeException e) {
                failed++;
                registerFailure(internalId, e);
            }
        }
        return new CleanupReport(marked, deleted, failed);
    }

    private int markUnreferenced() {
        List<Long> ids = internalRepository.findUnreferencedIds(ItemLifecycleState.ACTIVE, PageRequest.of(0, 500));
        ids.forEach(lifecycleService::onPersonalRemoved);
        return ids.size();
    }

    private List<Long> claimForDeletion() {
        return lifecycleService.claimForDeletion(properties.gracePeriod(), properties.batchSize());
    }

    private void deleteItem(long internalId) {
        Optional<ItemMetadataInternal> internal = internalRepository.findById(internalId);
        if (internal.isEmpty()) {
            return;
        }
        // 1. Content: storage objects first, rows second (each step repeatable)
        contentRevisionService.deleteContentNow(internalId);
        contentJobService.deleteJobsOf(internalId);
        // 2. Source media (copied from the modspace)
        mediaSourceSyncService.deleteAll(EntityType.ITEM, internalId);
        // 3. Modspace-specific data
        modspaceRegistry.find(internal.get().getSourceModspace())
                .ifPresent(adapter -> adapter.deleteModspaceData(internal.get().getOriginalId()));
        // 4. Projection and finally the identity itself
        transactionTemplate.executeWithoutResult(status -> {
            projectionService.delete(internalId);
            lifecycleService.deleteIdentity(internalId);
        });
    }

    private void registerFailure(long internalId, RuntimeException error) {
        log.warn("Cleanup of item {} failed: {}", internalId, error.getMessage());
        transactionTemplate.executeWithoutResult(status -> {
            Optional<ItemCleanup> found = cleanupRepository.findById(internalId);
            if (found.isEmpty()) {
                return;
            }
            ItemCleanup cleanup = found.get();
            short attempts = (short) (cleanup.getAttempts() + 1);
            cleanup.setAttempts(attempts);
            cleanup.setLastError(Texts.errorMessage(error));
            if (attempts >= properties.maxAttempts()) {
                cleanup.setNextAttemptAt(null);
                lifecycleService.markFailed(internalId);
                log.error("Cleanup of item {} moved to FAILED after {} attempts", internalId, attempts);
            } else {
                Duration delay = properties.retryBaseDelay().multipliedBy(1L << Math.min(10, attempts - 1));
                cleanup.setNextAttemptAt(OffsetDateTime.now(clock).plus(delay));
            }
            cleanupRepository.save(cleanup);
        });
    }

    public List<FailedItem> findFailed(int limit) {
        return transactionTemplate.execute(status -> {
            List<ItemMetadataInternal> items = internalRepository.findByLifecycleStateOrderByIdAsc(
                    ItemLifecycleState.FAILED, PageRequest.of(0, limit));
            Map<Long, ItemCleanup> cleanups = cleanupRepository.findAllById(
                            items.stream().map(ItemMetadataInternal::getId).toList())
                    .stream()
                    .collect(Collectors.toMap(ItemCleanup::getInternalMetadataId, Function.identity()));
            return items.stream().map(item -> new FailedItem(item, cleanups.get(item.getId()))).toList();
        });
    }

    public record CleanupReport(int marked, int deleted, int failed) {
    }

    public record FailedItem(ItemMetadataInternal internal, ItemCleanup cleanup) {
    }
}
