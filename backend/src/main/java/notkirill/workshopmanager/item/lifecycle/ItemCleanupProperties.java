package notkirill.workshopmanager.item.lifecycle;

import notkirill.workshopmanager.scheduler.ValidQuartzCron;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(ItemCleanupProperties.CONF_PATH)
@Validated
public record ItemCleanupProperties(
        @Name("enabled") Boolean enabled,
        @Name("cron") @ValidQuartzCron String cron,
        @Name("grace-period") Duration gracePeriod,
        @Name("batch-size") Integer batchSize,
        @Name("max-attempts") Integer maxAttempts,
        @Name("retry-base-delay") Duration retryBaseDelay
) {
    public static final String CONF_PATH = "application.items.cleanup";

    public ItemCleanupProperties {
        if (enabled == null) {
            enabled = true;
        }
        if (cron == null || cron.isBlank()) {
            cron = "0 15 * * * ? *";
        }
        if (gracePeriod == null) {
            gracePeriod = Duration.ofDays(7);
        }
        if (batchSize == null || batchSize <= 0) {
            batchSize = 50;
        }
        if (maxAttempts == null || maxAttempts <= 0) {
            maxAttempts = 5;
        }
        if (retryBaseDelay == null) {
            retryBaseDelay = Duration.ofMinutes(10);
        }
    }
}
