package notkirill.workshopmanager.media.source;

import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class MediaSourceSyncJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(MediaSourceSyncJob.class);

    private final MediaSourceSyncService syncService;

    @Autowired
    public MediaSourceSyncJob(MediaSourceSyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        int synced = syncService.syncDue();
        if (synced > 0) {
            log.info("Source media sync handled {} entit(y/ies)", synced);
        }
    }
}
