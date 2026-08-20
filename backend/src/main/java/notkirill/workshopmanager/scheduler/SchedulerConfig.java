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
import java.util.Set;
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
        Set<JobKey> activeJobKeys = activeJobDetails.stream()
                .map(JobDetail::getKey)
                .collect(Collectors.toSet());

        GroupMatcher<JobKey> groupMatcher = GroupMatcher.jobGroupEquals(schedulerProperties.jobGroupName());

        try {
            for (JobKey existingKey : scheduler.getJobKeys(groupMatcher)) {
                if (!activeJobKeys.contains(existingKey)) {
                    scheduler.deleteJob(existingKey);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to revalidate Quartz jobs", e);
        }
    }
}