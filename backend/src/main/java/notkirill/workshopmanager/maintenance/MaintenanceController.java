package notkirill.workshopmanager.maintenance;

import notkirill.workshopmanager.author.refresh.AuthorRefreshService;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.game.lookup.GameLookupService;
import notkirill.workshopmanager.item.lifecycle.ItemCleanupService;
import notkirill.workshopmanager.item.sync.ItemSyncService;
import notkirill.workshopmanager.media.enums.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/maintenance")
@PreAuthorize("hasAuthority('MAINTENANCE_MANAGE')")
public class MaintenanceController {
    private final MaintenanceWebService maintenanceWebService;

    @Autowired
    public MaintenanceController(MaintenanceWebService maintenanceWebService) {
        this.maintenanceWebService = maintenanceWebService;
    }

    @GetMapping
    public MaintenanceOverviewResponse overview() {
        return maintenanceWebService.overview();
    }

    @GetMapping("/cleanups/failed")
    public List<FailedCleanupResponse> failedCleanups() {
        return maintenanceWebService.failedCleanups();
    }

    @PostMapping("/cleanups/{internalId}/retry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retryCleanup(@PathVariable long internalId) {
        maintenanceWebService.retryCleanup(internalId);
    }

    @PostMapping("/cleanups/run")
    public ItemCleanupService.CleanupReport runCleanup() {
        return maintenanceWebService.runCleanup();
    }

    @PostMapping("/sync/run")
    public ItemSyncService.SyncReport runSync() {
        return maintenanceWebService.runSync();
    }

    @PostMapping("/authors/refresh/run")
    public AuthorRefreshService.RefreshReport runAuthorRefresh() {
        return maintenanceWebService.runAuthorRefresh();
    }

    @PostMapping("/games/lookup/run")
    public GameLookupService.LookupReport runGameLookup() {
        return maintenanceWebService.runGameLookup();
    }

    @GetMapping("/content-jobs")
    public List<ContentJobResponse> contentJobs(
            @RequestParam(name = "status", required = false) List<ContentJobStatus> statuses) {
        return maintenanceWebService.contentJobs(statuses);
    }

    @PostMapping("/content-jobs/{jobId}/retry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retryContentJob(@PathVariable long jobId) {
        maintenanceWebService.retryContentJob(jobId);
    }

    @PostMapping("/content-jobs/run")
    public Map<String, Integer> runContentJobs() {
        return Map.of("processed", maintenanceWebService.runContentJobs());
    }

    @GetMapping("/media-syncs/given-up")
    public List<FailedMediaSyncResponse> givenUpMediaSyncs() {
        return maintenanceWebService.givenUpMediaSyncs();
    }

    @PostMapping("/media-syncs/{entity}/{entityId}/retry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retryMediaSync(@PathVariable EntityType entity, @PathVariable long entityId) {
        maintenanceWebService.retryMediaSync(entity, entityId);
    }

    @PostMapping("/media-syncs/run")
    public Map<String, Integer> runMediaSyncs() {
        return Map.of("processed", maintenanceWebService.runMediaSyncs());
    }
}
