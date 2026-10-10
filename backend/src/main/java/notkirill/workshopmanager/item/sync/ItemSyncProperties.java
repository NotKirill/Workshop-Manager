package notkirill.workshopmanager.item.sync;

import notkirill.workshopmanager.scheduler.ValidQuartzCron;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(ItemSyncProperties.CONF_PATH)
@Validated
public record ItemSyncProperties(
        @Name("enabled") Boolean enabled,
        @Name("cron") @ValidQuartzCron String cron,
        @Name("stale-after") Duration staleAfter,
        @Name("batch-size") Integer batchSize
) {
    public static final String CONF_PATH = "application.items.sync";

    public ItemSyncProperties {
        if (enabled == null) {
            enabled = true;
        }
        if (cron == null || cron.isBlank()) {
            cron = "0 0 */6 * * ? *";
        }
        if (staleAfter == null) {
            staleAfter = Duration.ofHours(24);
        }
        if (batchSize == null || batchSize <= 0) {
            batchSize = 500;
        }
    }
}
