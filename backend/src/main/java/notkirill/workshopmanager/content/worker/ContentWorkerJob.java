package notkirill.workshopmanager.content.worker;

import notkirill.workshopmanager.ratelimit.CallPriority;
import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class ContentWorkerJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(ContentWorkerJob.class);

    private final ContentJobProcessor processor;

    @Autowired
    public ContentWorkerJob(ContentJobProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        int processed = CallPriority.BACKGROUND.call(processor::processDueJobs);
        if (processed > 0) {
            log.info("Content worker processed {} job(s)", processed);
        }
    }
}
