package notkirill.workshopmanager.maintenance;

import notkirill.workshopmanager.author.refresh.AuthorRefreshService;
import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.models.ContentJob;
import notkirill.workshopmanager.content.worker.ContentJobProcessor;
import notkirill.workshopmanager.game.lookup.GameLookupService;
import notkirill.workshopmanager.item.lifecycle.ItemCleanupService;
import notkirill.workshopmanager.item.lifecycle.ItemLifecycleService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import notkirill.workshopmanager.item.sync.ItemSyncService;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.models.MediaSourceSync;
import notkirill.workshopmanager.media.source.MediaSourceSyncService;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.steam.SteamProperties;
import notkirill.workshopmanager.storage.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MaintenanceWebService {
    private static final int LIST_LIMIT = 200;

    private final ItemProjectionService projectionService;
    private final ContentJobProcessor contentJobProcessor;
    private final ContentJobService contentJobService;
    private final ItemCleanupService cleanupService;
    private final ItemLifecycleService lifecycleService;
    private final ItemSyncService syncService;
    private final SteamProperties steamProperties;
    private final StorageProperties storageProperties;
    private final AuthorRefreshService authorRefreshService;
    private final GameLookupService gameLookupService;
    private final MediaSourceSyncService mediaSourceSyncService;

    @Autowired
    public MaintenanceWebService(ItemProjectionService projectionService, ContentJobProcessor contentJobProcessor,
                                 ContentJobService contentJobService, ItemCleanupService cleanupService,
                                 ItemLifecycleService lifecycleService, ItemSyncService syncService,
                                 SteamProperties steamProperties, StorageProperties storageProperties,
                                 AuthorRefreshService authorRefreshService, GameLookupService gameLookupService,
                                 MediaSourceSyncService mediaSourceSyncService) {
        this.projectionService = projectionService;
        this.contentJobProcessor = contentJobProcessor;
        this.contentJobService = contentJobService;
        this.cleanupService = cleanupService;
        this.lifecycleService = lifecycleService;
        this.syncService = syncService;
        this.steamProperties = steamProperties;
        this.storageProperties = storageProperties;
        this.authorRefreshService = authorRefreshService;
        this.gameLookupService = gameLookupService;
        this.mediaSourceSyncService = mediaSourceSyncService;
    }

    private static FailedCleanupResponse toCleanupResponse(ItemCleanupService.FailedItem failed) {
        ItemMetadataInternal internal = failed.internal();
        return new FailedCleanupResponse(
                internal.getId(), internal.getSourceModspace(), internal.getOriginalId(),
                internal.getLifecycleState(), failed.cleanup().getAttempts(), failed.cleanup().getLastError(),
                failed.cleanup().getOrphanedAt()
        );
    }

    private static String titleOf(Map<Long, ItemMetadataProjection> projections, long internalId) {
        return Optional.ofNullable(projections.get(internalId)).map(ItemMetadataProjection::getTitle).orElse(null);
    }

    public MaintenanceOverviewResponse overview() {
        return new MaintenanceOverviewResponse(
                steamProperties.hasWebApiKey(),
                contentJobProcessor.canFetch(ModspaceType.STEAM),
                storageProperties.type().name()
        );
    }

    public List<FailedCleanupResponse> failedCleanups() {
        return cleanupService.findFailed(LIST_LIMIT).stream()
                .map(MaintenanceWebService::toCleanupResponse)
                .toList();
    }

    public void retryCleanup(long internalId) {
        lifecycleService.retryFailed(internalId);
    }

    public ItemCleanupService.CleanupReport runCleanup() {
        return cleanupService.runCleanup();
    }

    public ItemSyncService.SyncReport runSync() {
        return syncService.syncStale();
    }

    public AuthorRefreshService.RefreshReport runAuthorRefresh() {
        return authorRefreshService.refreshQueued();
    }

    public GameLookupService.LookupReport runGameLookup() {
        return gameLookupService.lookUpQueued();
    }

    public List<ContentJobResponse> contentJobs(List<ContentJobStatus> statuses) {
        Collection<ContentJobStatus> filter = statuses == null || statuses.isEmpty()
                ? EnumSet.allOf(ContentJobStatus.class) : statuses;
        List<ContentJob> jobs = contentJobService.recentJobs(filter, LIST_LIMIT);
        Map<Long, ItemMetadataProjection> projections = projectionService.findAll(
                jobs.stream().map(ContentJob::getInternalMetadataId).distinct().toList());
        return jobs.stream()
                .map(job -> new ContentJobResponse(
                        job.getId(), job.getInternalMetadataId(),
                        titleOf(projections, job.getInternalMetadataId()),
                        job.getType(), job.getStatus(), job.getRevision(), job.getAttempts(), job.getNextAttemptAt(),
                        job.getLastError(), job.getNote(), job.getTimeCreated(), job.getTimeFinished()
                ))
                .toList();
    }

    public void retryContentJob(long jobId) {
        contentJobService.retry(jobId);
    }

    public int runContentJobs() {
        return contentJobProcessor.processDueJobs();
    }

    public List<FailedMediaSyncResponse> givenUpMediaSyncs() {
        List<MediaSourceSync> syncs = mediaSourceSyncService.givenUp();
        Map<Long, ItemMetadataProjection> projections = projectionService.findAll(syncs.stream()
                                                                                          .filter(sync -> sync.getEntity() == EntityType.ITEM).map(
                        MediaSourceSync::getEntityId).toList());
        return syncs.stream()
                .map(sync -> new FailedMediaSyncResponse(
                        sync.getEntity(), sync.getEntityId(),
                        sync.getEntity() == EntityType.ITEM ? titleOf(projections, sync.getEntityId()) : null,
                        sync.getFailures(), sync.getLastError(), sync.getTimeSynced()
                ))
                .toList();
    }

    public void retryMediaSync(EntityType entity, long entityId) {
        mediaSourceSyncService.retry(entity, entityId);
    }

    public int runMediaSyncs() {
        return mediaSourceSyncService.syncDue();
    }
}
