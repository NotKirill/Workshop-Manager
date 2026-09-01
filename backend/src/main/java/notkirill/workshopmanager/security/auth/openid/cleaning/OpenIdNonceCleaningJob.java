package notkirill.workshopmanager.security.auth.openid.cleaning;

import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class OpenIdNonceCleaningJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(OpenIdNonceCleaningJob.class);

    private final OpenIdNonceCleaningService cleaningService;

    @Autowired
    public OpenIdNonceCleaningJob(OpenIdNonceCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        long deletedCount = cleaningService.clearOldNonce();
        log.info("OpenID nonce cleanup: deleted {} nonces past retention period", deletedCount);
    }
}
