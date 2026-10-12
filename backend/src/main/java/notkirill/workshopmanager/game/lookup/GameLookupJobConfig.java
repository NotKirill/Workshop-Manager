package notkirill.workshopmanager.game.lookup;

import notkirill.workshopmanager.scheduler.SchedulerProperties;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameLookupJobConfig {
    private final SchedulerProperties schedulerProperties;
    private final GameLookupProperties lookupProperties;

    @Autowired
    public GameLookupJobConfig(SchedulerProperties schedulerProperties, GameLookupProperties lookupProperties) {
        this.schedulerProperties = schedulerProperties;
        this.lookupProperties = lookupProperties;
    }

    @Bean
    @Conditional(GameLookupJobCondition.class)
    public JobDetail gameLookupJobDetail() {
        return JobBuilder
                .newJob(GameLookupJob.class)
                .withIdentity("gameLookupJob", schedulerProperties.jobGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    @Conditional(GameLookupJobCondition.class)
    public Trigger gameLookupJobTrigger(@Qualifier("gameLookupJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("gameLookupJobTrigger", schedulerProperties.jobGroupName())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(lookupProperties.interval().toMillis())
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }
}
