package notkirill.workshopmanager.author.refresh;

import notkirill.workshopmanager.scheduler.ValidQuartzCron;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static notkirill.workshopmanager.author.refresh.AuthorRefreshProperties.CONF_PATH;

@ConfigurationProperties(CONF_PATH)
@Validated
public record AuthorRefreshProperties(
        @Name("enabled") Boolean enabled,
        @Name("cron") @ValidQuartzCron String cron,
        @Name("stale-after") Duration staleAfter,
        @Name("batch-size") Integer batchSize
) {
    public static final String CONF_PATH = "application.authors.refresh";

    public AuthorRefreshProperties {
        if (enabled == null) {
            enabled = true;
        }
        if (cron == null || cron.isBlank()) {
            cron = "0 45 * * * ? *";
        }
        if (staleAfter == null) {
            staleAfter = Duration.ofDays(7);
        }
        if (batchSize == null || batchSize <= 0) {
            batchSize = 30;
        }
    }
}
