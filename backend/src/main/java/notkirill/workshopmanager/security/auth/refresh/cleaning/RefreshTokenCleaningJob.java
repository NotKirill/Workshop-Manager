package notkirill.workshopmanager.security.auth.refresh.cleaning;

import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class RefreshTokenCleaningJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleaningJob.class);

    private final RefreshTokenCleaningService cleaningService;

    @Autowired
    public RefreshTokenCleaningJob(RefreshTokenCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        long deletedCount = cleaningService.clearOldExpiredTokens();
        log.info("Refresh token cleanup: deleted {} tokens past retention period", deletedCount);
    }
}
