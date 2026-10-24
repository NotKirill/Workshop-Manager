package notkirill.workshopmanager.item.lifecycle;

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
public class ItemCleanupJobConfig {
    private final SchedulerProperties schedulerProperties;
    private final ItemCleanupProperties cleanupProperties;

    @Autowired
    public ItemCleanupJobConfig(SchedulerProperties schedulerProperties, ItemCleanupProperties cleanupProperties) {
        this.schedulerProperties = schedulerProperties;
        this.cleanupProperties = cleanupProperties;
    }

    @Bean
    @Conditional(ItemCleanupJobCondition.class)
    public JobDetail itemCleanupJobDetail() {
        return JobBuilder
                .newJob(ItemCleanupJob.class)
                .withIdentity("itemCleanupJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(ItemCleanupJobCondition.class)
    public Trigger itemCleanupJobTrigger(@Qualifier("itemCleanupJobDetail") JobDetail jobDetail, Clock clock) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("itemCleanupJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(cleanupProperties.cron())
                        .withMisfireHandlingInstructionFireAndProceed()
                        .inTimeZone(TimeZone.getTimeZone(clock.getZone())))
                .build();
    }
}
