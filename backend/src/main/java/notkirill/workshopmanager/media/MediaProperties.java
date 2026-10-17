package notkirill.workshopmanager.media;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("application.media")
@Validated
public record MediaProperties(
        @Name("max-image-size") @NotNull DataSize maxImageSize,
        @Name("download-link-ttl") @NotNull @DurationMin(seconds = 1) Duration downloadLinkTtl,
        @Name("sync-enabled") @NotNull Boolean syncEnabled,
        @Name("sync-interval") @NotNull @DurationMin(seconds = 1) Duration syncInterval,
        @Name("sync-batch-size") @NotNull @Min(1) Integer syncBatchSize,
        @Name("sync-max-failures") @NotNull @Min(1) Short syncMaxFailures,
        @Name("sync-retry-base-delay") @NotNull Duration syncRetryBaseDelay,
        @Name("download-timeout") @NotNull @DurationMin(seconds = 1) Duration downloadTimeout) {
}
