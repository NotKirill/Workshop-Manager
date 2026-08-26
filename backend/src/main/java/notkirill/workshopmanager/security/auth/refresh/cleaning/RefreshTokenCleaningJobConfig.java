package notkirill.workshopmanager.security.auth.refresh.cleaning;

import notkirill.workshopmanager.scheduler.SchedulerProperties;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenProperties;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class RefreshTokenCleaningJobConfig {
    private final SchedulerProperties schedulerProperties;
    private final RefreshTokenProperties refreshTokenProperties;

    @Autowired
    public RefreshTokenCleaningJobConfig(SchedulerProperties schedulerProperties,
                                         RefreshTokenProperties refreshTokenProperties) {
        this.schedulerProperties = schedulerProperties;
        this.refreshTokenProperties = refreshTokenProperties;
    }

    @Bean
    @Conditional(RefreshTokenCleaningJobCondition.class)
    public JobDetail refreshTokenCleaningJobDetail() {
        return JobBuilder
                .newJob(RefreshTokenCleaningJob.class)
                .withIdentity("refreshTokenCleaningJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(RefreshTokenCleaningJobCondition.class)
    public Trigger refreshTokenCleaningTrigger(JobDetail refreshTokenCleaningJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(refreshTokenCleaningJobDetail)
                .withIdentity("refreshTokenCleaningJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(CronScheduleBuilder
                                      .cronSchedule(refreshTokenProperties.cleaningJob().cron())
                                      .withMisfireHandlingInstructionFireAndProceed()
                                      .inTimeZone(TimeZone.getTimeZone("UTC")))
                .build();
    }
}
