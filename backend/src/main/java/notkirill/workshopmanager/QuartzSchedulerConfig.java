package notkirill.workshopmanager;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class QuartzSchedulerConfig implements SmartInitializingSingleton {
    private final Scheduler scheduler;

    @Autowired
    public QuartzSchedulerConfig(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void afterSingletonsInstantiated() {
        revalidateJobs();
    }

    private void revalidateJobs() {
        List<JobDetail> jobDetails = getAllJobDetails();
        Set<JobKey> currentJobKeys = jobDetails.stream()
                .map(JobDetail::getKey)
                .collect(Collectors.toSet());

        GroupMatcher<JobKey> groupMatcher = GroupMatcher.anyGroup();

        try {
            for (JobKey existingKey : scheduler.getJobKeys(groupMatcher)) {
                if (!currentJobKeys.contains(existingKey)) {
                    scheduler.deleteJob(existingKey);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to revalidate Quartz jobs", e);
        }
    }

    private List<JobDetail> getAllJobDetails() {
        List<JobDetail> details = new ArrayList<>();

        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
                JobDetail detail = scheduler.getJobDetail(jobKey);
                if (detail != null) {
                    details.add(detail);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to get Quartz job details", e);
        }

        return details;
    }
}