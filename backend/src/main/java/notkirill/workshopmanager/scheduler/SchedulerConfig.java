package notkirill.workshopmanager.scheduler;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class SchedulerConfig implements SmartInitializingSingleton {
    private final Scheduler scheduler;
    private final SchedulerProperties schedulerProperties;
    private final List<JobDetail> activeJobDetails;
    private final List<Trigger> activeTriggers;

    @Autowired
    public SchedulerConfig(Scheduler scheduler, SchedulerProperties schedulerProperties,
                           List<JobDetail> activeJobDetails, List<Trigger> activeTriggers) {
        this.scheduler = scheduler;
        this.schedulerProperties = schedulerProperties;
        this.activeJobDetails = activeJobDetails;
        this.activeTriggers = activeTriggers;
    }

    @Override
    public void afterSingletonsInstantiated() {
        revalidateJobs();
        revalidateTriggers();
    }

    private void revalidateJobs() {
        Map<JobKey, JobDetail> activeJobs = activeJobDetails.stream().collect(
                Collectors.toMap(JobDetail::getKey, Function.identity()));

        GroupMatcher<JobKey> groupMatcher = GroupMatcher.jobGroupEquals(schedulerProperties.jobGroupName());

        try {
            for (JobKey existingKey : scheduler.getJobKeys(groupMatcher)) {
                if (!activeJobs.containsKey(existingKey)) {
                    scheduler.deleteJob(existingKey);
                    continue;
                }

                JobDetail existingJob = scheduler.getJobDetail(existingKey);
                JobDetail activeJob = activeJobs.get(existingKey);

                if (!areJobDetailsEqual(existingJob, activeJob)) {
                    // Long TODO make filtering for specific data
                    activeJob.getJobDataMap().putAll(existingJob.getJobDataMap());
                    scheduler.addJob(activeJob, true);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to revalidate Quartz jobs", e);
        }
    }

    private boolean areJobDetailsEqual(JobDetail jd1, JobDetail jd2) {
        if (jd1 == null || jd2 == null) {
            throw new IllegalArgumentException("Job Details must not be null");
        }

        return Objects.equals(jd1.getDescription(), jd2.getDescription()) &&
                jd1.getJobClass() == jd2.getJobClass() &&
                jd1.isDurable() == jd2.isDurable() &&
                jd1.isPersistJobDataAfterExecution() == jd2.isPersistJobDataAfterExecution() &&
                jd1.isConcurrentExecutionDisallowed() == jd2.isConcurrentExecutionDisallowed() &&
                jd1.requestsRecovery() == jd2.requestsRecovery();

    }

    private void revalidateTriggers() {
        Map<TriggerKey, Trigger> activeTriggersByKey = activeTriggers.stream().collect(
                Collectors.toMap(Trigger::getKey, Function.identity()));

        GroupMatcher<TriggerKey> groupMatcher = GroupMatcher.triggerGroupEquals(schedulerProperties.jobGroupName());

        try {
            for (TriggerKey existingKey : scheduler.getTriggerKeys(groupMatcher)) {
                Trigger activeTrigger = activeTriggersByKey.get(existingKey);
                if (activeTrigger == null) {
                    scheduler.unscheduleJob(existingKey);
                    continue;
                }

                Trigger existingTrigger = scheduler.getTrigger(existingKey);
                if (!areTriggersEqual(existingTrigger, activeTrigger)) {
                    scheduler.rescheduleJob(existingKey, activeTrigger);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to revalidate Quartz triggers", e);
        }
    }

    private boolean areTriggersEqual(Trigger t1, Trigger t2) {
        if (t1 == null || t2 == null) {
            throw new IllegalArgumentException("Triggers must not be null");
        }

        boolean commonEqual = Objects.equals(t1.getJobKey(), t2.getJobKey()) &&
                Objects.equals(t1.getDescription(), t2.getDescription()) &&
                Objects.equals(t1.getCalendarName(), t2.getCalendarName()) &&
                Objects.equals(t1.getEndTime(), t2.getEndTime()) &&
                t1.getPriority() == t2.getPriority() &&
                t1.getMisfireInstruction() == t2.getMisfireInstruction();

        if (!commonEqual) {
            return false;
        }

        if (t1 instanceof CronTrigger c1 && t2 instanceof CronTrigger c2) {
            return Objects.equals(c1.getCronExpression(), c2.getCronExpression()) &&
                    Objects.equals(c1.getTimeZone().getID(), c2.getTimeZone().getID());
        }

        if (t1 instanceof SimpleTrigger s1 && t2 instanceof SimpleTrigger s2) {
            return s1.getRepeatInterval() == s2.getRepeatInterval() &&
                    s1.getRepeatCount() == s2.getRepeatCount();
        }

        return false;
    }
}