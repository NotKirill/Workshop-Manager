package notkirill.workshopmanager.scheduler;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("application.scheduler")
@Validated
public record SchedulerProperties(
        @Name("job-group-name") @NotBlank String jobGroupName) {
}
