package notkirill.workshopmanager.author.refresh;

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
public class AuthorRefreshJobConfig {
    private final SchedulerProperties schedulerProperties;
    private final AuthorRefreshProperties refreshProperties;

    @Autowired
    public AuthorRefreshJobConfig(SchedulerProperties schedulerProperties, AuthorRefreshProperties refreshProperties) {
        this.schedulerProperties = schedulerProperties;
        this.refreshProperties = refreshProperties;
    }

    @Bean
    @Conditional(AuthorRefreshJobCondition.class)
    public JobDetail authorRefreshJobDetail() {
        return JobBuilder
                .newJob(AuthorRefreshJob.class)
                .withIdentity("authorRefreshJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(AuthorRefreshJobCondition.class)
    public Trigger authorRefreshJobTrigger(@Qualifier("authorRefreshJobDetail") JobDetail jobDetail, Clock clock) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("authorRefreshJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(refreshProperties.cron())
                        .withMisfireHandlingInstructionDoNothing()
                        .inTimeZone(TimeZone.getTimeZone(clock.getZone())))
                .build();
    }
}
