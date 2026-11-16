package notkirill.workshopmanager.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.jspecify.annotations.NonNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class JobRunMetrics implements JobListener {
    private final MeterRegistry meterRegistry;

    @Autowired
    public JobRunMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public SchedulerFactoryBeanCustomizer jobRunMetricsListener() {
        return factory -> factory.setGlobalJobListeners(this);
    }

    @Override
    @NonNull
    public String getName() {
        return "job-run-metrics";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // Only finished runs are measured
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // No trigger listener vetoes runs
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        Timer.builder("workshop.jobs")
                .description("Scheduled job runs")
                .tag("job", context.getJobDetail().getKey().getName())
                .tag("outcome", exception == null ? "SUCCESS" : "FAILURE")
                .register(meterRegistry)
                .record(context.getJobRunTime(), TimeUnit.MILLISECONDS);
    }
}
