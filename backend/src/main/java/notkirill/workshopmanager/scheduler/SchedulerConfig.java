package notkirill.workshopmanager.scheduler;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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

    @Autowired
    public SchedulerConfig(Scheduler scheduler, SchedulerProperties schedulerProperties,
                           List<JobDetail> activeJobDetails) {
        this.scheduler = scheduler;
        this.schedulerProperties = schedulerProperties;
        this.activeJobDetails = activeJobDetails;
    }

    @Override
    public void afterSingletonsInstantiated() {
        revalidateJobs();
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
}