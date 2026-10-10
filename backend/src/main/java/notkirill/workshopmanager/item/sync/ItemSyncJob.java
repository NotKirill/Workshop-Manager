package notkirill.workshopmanager.item.sync;

import notkirill.workshopmanager.ratelimit.CallPriority;
import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class ItemSyncJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(ItemSyncJob.class);

    private final ItemSyncService syncService;

    @Autowired
    public ItemSyncJob(ItemSyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        ItemSyncService.SyncReport report = CallPriority.BACKGROUND.call(syncService::syncStale);
        if (report.synced() + report.unavailable() > 0) {
            log.info(
                    "Item sync: {} synced, {} unavailable, {} backup(s) queued",
                    report.synced(), report.unavailable(), report.backupsQueued()
            );
        }
    }
}
