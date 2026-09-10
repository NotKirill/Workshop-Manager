package notkirill.workshopmanager.security.auth.refresh;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.scheduler.ValidQuartzCron;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static notkirill.workshopmanager.security.auth.refresh.RefreshTokenProperties.CONF_PATH;

@ConfigurationProperties(CONF_PATH)
@Validated
public record RefreshTokenProperties(
        @Name("expiration-seconds") @NotNull @Min(value = 1) Integer expirationSeconds,
        @Name("cleaning-job") @Valid CleaningJob cleaningJob) {
    public static final String CONF_PATH = "application.security.refresh-token";

    public RefreshTokenProperties {
        if (cleaningJob == null) {
            cleaningJob = new CleaningJob(null, null, null);
        }
    }

    public record CleaningJob(
            @Name("enabled") Boolean enabled,
            @Name("cron") @ValidQuartzCron String cron,
            @Name("retention-after-expiration") Duration retentionAfterExpiration
    ) {
        public CleaningJob {
            if (enabled == null) {
                enabled = false;
                cron = null;
                retentionAfterExpiration = null;
            }

            if (enabled) {
                if (cron == null || cron.isBlank()) {
                    throw new IllegalArgumentException("Cron expression cannot be empty when job is enabled");
                }
                if (retentionAfterExpiration == null) {
                    throw new IllegalArgumentException("Retention cannot be empty when job is enabled");
                }
            }
        }
    }
}
