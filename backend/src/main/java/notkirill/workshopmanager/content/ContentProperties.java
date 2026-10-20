package notkirill.workshopmanager.content;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.scheduler.ValidQuartzCron;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.time.Duration;

@ConfigurationProperties(ContentProperties.CONF_PATH)
@Validated
public record ContentProperties(
        @Name("temp-dir") Path tempDir,
        @Name("download-link-ttl") Duration downloadLinkTtl,
        @Name("default-max-revisions") Short defaultMaxRevisions,
        @Name("housekeeping-cron") @ValidQuartzCron String housekeepingCron,

        @Name("jobs") @DefaultValue @Valid Jobs jobs,
        @Name("uploads") @DefaultValue @Valid Uploads uploads
) {
    public static final String CONF_PATH = "application.content";

    public ContentProperties {
        if (defaultMaxRevisions == null || defaultMaxRevisions <= 0) {
            defaultMaxRevisions = 3;
        }
        if (housekeepingCron == null || housekeepingCron.isBlank()) {
            housekeepingCron = "0 30 3 * * ? *";
        }
        if (downloadLinkTtl == null) {
            downloadLinkTtl = Duration.ofMinutes(10);
        }
        if (tempDir == null) {
            tempDir = Path.of(System.getProperty("java.io.tmpdir"), "workshop-manager", "content");
        }
    }

    public record Jobs(
            @Name("enabled") Boolean enabled,
            @Name("interval") Duration interval,
            @Name("batch-size") Integer batchSize,
            @Name("max-attempts") Integer maxAttempts,
            @Name("retry-base-delay") Duration retryBaseDelay,
            @Name("running-timeout") Duration runningTimeout,
            @Name("history-retention") @NotNull Duration historyRetention
    ) {
        public Jobs {
            if (enabled == null) {
                enabled = true;
            }
            if (interval == null) {
                interval = Duration.ofSeconds(30);
            }
            if (batchSize == null || batchSize <= 0) {
                batchSize = 3;
            }
            if (maxAttempts == null || maxAttempts <= 0) {
                maxAttempts = 5;
            }
            if (retryBaseDelay == null) {
                retryBaseDelay = Duration.ofMinutes(1);
            }
            if (runningTimeout == null) {
                runningTimeout = Duration.ofHours(2);
            }
        }
    }

    public record Uploads(
            @Name("max-size") @NotNull DataSize maxSize,
            @Name("chunk-size") @NotNull DataSize chunkSize,
            @Name("expiry") @NotNull Duration expiry
    ) {
    }
}
