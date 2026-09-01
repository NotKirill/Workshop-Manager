package notkirill.workshopmanager.security.auth.openid.cleaning;

import notkirill.workshopmanager.scheduler.SchedulerProperties;
import notkirill.workshopmanager.security.auth.openid.OpenIdProperties;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.TimeZone;

@Configuration
public class OpenIdNonceCleaningJobConfig {
    private final SchedulerProperties schedulerProperties;
    private final OpenIdProperties openIdProperties;

    @Autowired
    public OpenIdNonceCleaningJobConfig(SchedulerProperties schedulerProperties,
                                        OpenIdProperties openIdProperties) {
        this.schedulerProperties = schedulerProperties;
        this.openIdProperties = openIdProperties;
    }

    @Bean
    @Conditional(OpenIdNonceCleaningJobCondition.class)
    public JobDetail openIdNonceCleaningJobDetail() {
        return JobBuilder
                .newJob(OpenIdNonceCleaningJob.class)
                .withIdentity("openIdNonceCleaningJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(OpenIdNonceCleaningJobCondition.class)
    public Trigger openIdNonceCleaningJobTrigger(
            @Qualifier("openIdNonceCleaningJobDetail") JobDetail openIdNonceCleaningJobDetail, Clock clock) {
        return TriggerBuilder.newTrigger()
                .forJob(openIdNonceCleaningJobDetail)
                .withIdentity("openIdNonceCleaningJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(CronScheduleBuilder
                                      .cronSchedule(openIdProperties.nonce().cleaningJob().cron())
                                      .withMisfireHandlingInstructionFireAndProceed()
                                      .inTimeZone(TimeZone.getTimeZone(clock.getZone())))
                .build();
    }
}
