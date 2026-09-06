package notkirill.workshopmanager.scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerConfigTest {
    private static final String GROUP = "TEST";
    private static final SchedulerProperties PROPERTIES = new SchedulerProperties(GROUP);

    private Scheduler scheduler;

    private static JobDetail job(String name) {
        return JobBuilder.newJob(NoopJob.class).withIdentity(name, GROUP).storeDurably().build();
    }

    private static Trigger cron(String name, JobDetail job, String cron) {
        return TriggerBuilder.newTrigger().forJob(job).withIdentity(name, GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                .build();
    }

    private static Trigger interval(String name, JobDetail job, long millis) {
        return TriggerBuilder.newTrigger().forJob(job).withIdentity(name, GROUP)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(millis)
                                      .repeatForever().withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }

    @BeforeEach
    void setUp() throws SchedulerException {
        Properties quartz = new Properties();
        quartz.setProperty("org.quartz.scheduler.instanceName", "test-" + UUID.randomUUID());
        quartz.setProperty("org.quartz.threadPool.threadCount", "1");
        quartz.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        scheduler = new StdSchedulerFactory(quartz).getScheduler();
    }

    @AfterEach
    void tearDown() throws SchedulerException {
        scheduler.shutdown();
    }

    @Test
    void changedCronIsRescheduled() throws SchedulerException {
        JobDetail job = job("a");
        scheduler.scheduleJob(job, cron("a", job, "0 0 0 1 * ?"));

        revalidate(List.of(job), List.of(cron("a", job, "0 15 * * * ?")));

        CronTrigger stored = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("a", GROUP));
        assertThat(stored.getCronExpression()).isEqualTo("0 15 * * * ?");
    }

    @Test
    void changedMisfireRuleIsRescheduled() throws SchedulerException {
        JobDetail job = job("a");
        scheduler.scheduleJob(job, cron("a", job, "0 15 * * * ?"));

        Trigger active = TriggerBuilder.newTrigger().forJob(job).withIdentity("a", GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 15 * * * ?")
                                      .withMisfireHandlingInstructionFireAndProceed())
                .build();
        revalidate(List.of(job), List.of(active));

        assertThat(scheduler.getTrigger(TriggerKey.triggerKey("a", GROUP)).getMisfireInstruction())
                .isEqualTo(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
    }

    @Test
    void changedIntervalIsRescheduled() throws SchedulerException {
        JobDetail job = job("a");
        scheduler.scheduleJob(job, interval("a", job, 30_000));

        revalidate(List.of(job), List.of(interval("a", job, 10_000)));

        SimpleTrigger stored = (SimpleTrigger) scheduler.getTrigger(TriggerKey.triggerKey("a", GROUP));
        assertThat(stored.getRepeatInterval()).isEqualTo(10_000);
    }

    @Test
    void unchangedTriggerKeepsItsStoredFireTimes() throws SchedulerException {
        JobDetail job = job("a");
        Trigger original = cron("a", job, "0 15 * * * ?");
        scheduler.scheduleJob(job, original);
        Trigger before = scheduler.getTrigger(original.getKey());

        // The bean is built again on every start, with a new start time: far enough that a reschedule would show
        Trigger active = TriggerBuilder.newTrigger().forJob(job).withIdentity("a", GROUP)
                .startAt(new Date(System.currentTimeMillis() + Duration.ofHours(2).toMillis()))
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 15 * * * ?").withMisfireHandlingInstructionDoNothing())
                .build();
        revalidate(List.of(job), List.of(active));

        Trigger after = scheduler.getTrigger(original.getKey());
        assertThat(after.getStartTime()).isEqualTo(before.getStartTime());
        assertThat(after.getNextFireTime()).isEqualTo(before.getNextFireTime());
    }

    @Test
    void triggerThatIsNoLongerABeanIsRemovedAndItsJobKept() throws SchedulerException {
        JobDetail job = job("a");
        scheduler.scheduleJob(job, cron("oldName", job, "0 15 * * * ?"));
        Trigger renamed = cron("newName", job, "0 15 * * * ?");
        scheduler.scheduleJob(renamed);

        revalidate(List.of(job), List.of(renamed));

        assertThat(scheduler.getTriggersOfJob(job.getKey())).extracting(Trigger::getKey)
                .containsExactly(renamed.getKey());
    }

    @Test
    void jobThatIsNoLongerABeanIsDeletedWithItsTrigger() throws SchedulerException {
        JobDetail kept = job("kept");
        JobDetail removed = job("removed");
        scheduler.scheduleJob(kept, cron("kept", kept, "0 15 * * * ?"));
        scheduler.scheduleJob(removed, cron("removed", removed, "0 15 * * * ?"));

        revalidate(List.of(kept), List.of(cron("kept", kept, "0 15 * * * ?")));

        assertThat(scheduler.checkExists(removed.getKey())).isFalse();
        assertThat(scheduler.checkExists(TriggerKey.triggerKey("removed", GROUP))).isFalse();
        assertThat(scheduler.checkExists(TriggerKey.triggerKey("kept", GROUP))).isTrue();
    }

    private void revalidate(List<JobDetail> jobs, List<Trigger> triggers) {
        new SchedulerConfig(scheduler, PROPERTIES, jobs, triggers).afterSingletonsInstantiated();
    }

    public static class NoopJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
        }
    }
}
