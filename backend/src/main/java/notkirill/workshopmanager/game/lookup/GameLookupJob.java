package notkirill.workshopmanager.game.lookup;

import notkirill.workshopmanager.ratelimit.CallPriority;
import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class GameLookupJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(GameLookupJob.class);

    private final GameLookupService lookupService;

    @Autowired
    public GameLookupJob(GameLookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        GameLookupService.LookupReport report = CallPriority.BACKGROUND.call(lookupService::lookUpQueued);
        // Runs every minute: only runs that did something are logged
        if (report.named() + report.unknown() + report.failed() > 0) {
            log.info("Game lookup: {} named, {} unknown to the modspace, {} failed",
                    report.named(), report.unknown(), report.failed());
        }
    }
}
