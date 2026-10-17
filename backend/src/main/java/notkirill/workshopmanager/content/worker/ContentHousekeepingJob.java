package notkirill.workshopmanager.content.worker;

import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.content.ContentProperties;
import org.jspecify.annotations.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class ContentHousekeepingJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(ContentHousekeepingJob.class);

    private final ContentJobService contentJobService;
    private final ContentProperties properties;

    @Autowired
    public ContentHousekeepingJob(ContentJobService contentJobService, ContentProperties properties) {
        this.contentJobService = contentJobService;
        this.properties = properties;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        int pruned = contentJobService.pruneFinished(properties.jobs().historyRetention());
        log.info("Content housekeeping: {} old job(s) pruned", pruned);
    }
}
