package notkirill.workshopmanager.security.auth.openid;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.scheduler.ValidQuartzCron;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(OpenIdProperties.CONF_PATH)
@Validated
public record OpenIdProperties(
        @Name("nonce") @Valid Nonce nonce,
        @Name("link-token-ttl") @NotNull Duration linkTokenTtl
) {
    public static final String CONF_PATH = "application.security.openid";

    public OpenIdProperties {
        if (nonce == null) {
            nonce = new Nonce(5, 5, null);
        }
    }

    public record Nonce(
            @Name("max-age-minutes") @NotNull @Min(0) Integer maxAgeMinutes,
            @Name("allowed-future-skew-minutes") @NotNull @Min(0) Integer allowedFutureSkewMinutes,
            @Name("cleaning-job") @Valid Nonce.CleaningJob cleaningJob
    ) {
        public Nonce {
            if (cleaningJob == null) {
                cleaningJob = new CleaningJob(null, null, null);
            }
        }

        // Long TODO maybe abstract for all cleaning jobs
        public record CleaningJob(
                @Name("enabled") Boolean enabled,
                @Name("cron") @ValidQuartzCron String cron,
                @Name("retention") Duration retention
        ) {
            public CleaningJob {
                if (enabled == null) {
                    enabled = false;
                    cron = null;
                    retention = null;
                }

                if (enabled) {
                    if (cron == null || cron.isBlank()) {
                        throw new IllegalArgumentException("Cron expression cannot be empty when job is enabled");
                    }
                    if (retention == null) {
                        throw new IllegalArgumentException("Retention cannot be empty when job is enabled");
                    }
                }
            }
        }
    }
}
