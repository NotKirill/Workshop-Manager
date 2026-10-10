package notkirill.workshopmanager.author.refresh;

import notkirill.workshopmanager.ratelimit.CallPriority;
import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class AuthorRefreshJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(AuthorRefreshJob.class);

    private final AuthorRefreshService refreshService;

    @Autowired
    public AuthorRefreshJob(AuthorRefreshService refreshService) {
        this.refreshService = refreshService;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        AuthorRefreshService.RefreshReport report = CallPriority.BACKGROUND.call(refreshService::refreshQueued);
        if (report.refreshed() + report.failed() > 0) {
            log.info("Author refresh: {} refreshed, {} failed", report.refreshed(), report.failed());
        }
    }
}
