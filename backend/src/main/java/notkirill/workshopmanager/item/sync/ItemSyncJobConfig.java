package notkirill.workshopmanager.item.sync;

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
public class ItemSyncJobConfig {
    private final SchedulerProperties schedulerProperties;
    private final ItemSyncProperties syncProperties;

    @Autowired
    public ItemSyncJobConfig(SchedulerProperties schedulerProperties, ItemSyncProperties syncProperties) {
        this.schedulerProperties = schedulerProperties;
        this.syncProperties = syncProperties;
    }

    @Bean
    @Conditional(ItemSyncJobCondition.class)
    public JobDetail itemSyncJobDetail() {
        return JobBuilder
                .newJob(ItemSyncJob.class)
                .withIdentity("itemSyncJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(ItemSyncJobCondition.class)
    public Trigger itemSyncJobTrigger(@Qualifier("itemSyncJobDetail") JobDetail jobDetail, Clock clock) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("itemSyncJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(CronScheduleBuilder
                                      .cronSchedule(syncProperties.cron())
                                      .withMisfireHandlingInstructionDoNothing()
                                      .inTimeZone(TimeZone.getTimeZone(clock.getZone())))
                .build();
    }
}
