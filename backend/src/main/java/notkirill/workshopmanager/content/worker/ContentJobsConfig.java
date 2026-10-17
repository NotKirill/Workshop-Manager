package notkirill.workshopmanager.content.worker;

import notkirill.workshopmanager.content.ContentProperties;
import notkirill.workshopmanager.scheduler.SchedulerProperties;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.TimeZone;

@Configuration
public class ContentJobsConfig {
    private final SchedulerProperties schedulerProperties;
    private final ContentProperties contentProperties;

    @Autowired
    public ContentJobsConfig(SchedulerProperties schedulerProperties, ContentProperties contentProperties) {
        this.schedulerProperties = schedulerProperties;
        this.contentProperties = contentProperties;
    }

    @Bean
    @Conditional(ContentJobsCondition.class)
    public JobDetail contentWorkerJobDetail() {
        return JobBuilder
                .newJob(ContentWorkerJob.class)
                .withIdentity("contentWorkerJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(ContentJobsCondition.class)
    public Trigger contentWorkerJobTrigger(@Qualifier("contentWorkerJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("contentWorkerJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                      .withIntervalInMilliseconds(contentProperties.jobs().interval().toMillis())
                                      .repeatForever()
                                      .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }

    @Bean
    @Conditional(ContentJobsCondition.class)
    public JobDetail contentHousekeepingJobDetail() {
        return JobBuilder
                .newJob(ContentHousekeepingJob.class)
                .withIdentity("contentHousekeepingJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(ContentJobsCondition.class)
    public Trigger contentHousekeepingJobTrigger(@Qualifier("contentHousekeepingJobDetail") JobDetail jobDetail,
                                                 Clock clock) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("contentHousekeepingJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(CronScheduleBuilder
                                      .cronSchedule(contentProperties.housekeepingCron())
                                      .withMisfireHandlingInstructionFireAndProceed()
                                      .inTimeZone(TimeZone.getTimeZone(clock.getZone())))
                .build();
    }
}
