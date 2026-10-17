package notkirill.workshopmanager.media.source;

import notkirill.workshopmanager.media.MediaProperties;
import notkirill.workshopmanager.scheduler.SchedulerProperties;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediaSourceSyncConfig {
    private final SchedulerProperties schedulerProperties;
    private final MediaProperties mediaProperties;

    @Autowired
    public MediaSourceSyncConfig(SchedulerProperties schedulerProperties, MediaProperties mediaProperties) {
        this.schedulerProperties = schedulerProperties;
        this.mediaProperties = mediaProperties;
    }

    @Bean
    @Conditional(MediaSourceSyncCondition.class)
    public JobDetail mediaSourceSyncJobDetail() {
        return JobBuilder
                .newJob(MediaSourceSyncJob.class)
                .withIdentity("mediaSourceSyncJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(MediaSourceSyncCondition.class)
    public Trigger mediaSourceSyncJobTrigger(@Qualifier("mediaSourceSyncJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("mediaSourceSyncJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(mediaProperties.syncInterval().toMillis())
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }
}
