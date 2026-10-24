package notkirill.workshopmanager.item.lifecycle;

import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class ItemCleanupJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(ItemCleanupJob.class);

    private final ItemCleanupService cleanupService;

    @Autowired
    public ItemCleanupJob(ItemCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        ItemCleanupService.CleanupReport report = cleanupService.runCleanup();
        if (report.marked() + report.deleted() + report.failed() > 0) {
            log.info(
                    "Item cleanup: {} marked orphaned, {} deleted, {} failed",
                    report.marked(), report.deleted(), report.failed()
            );
        }
    }
}
