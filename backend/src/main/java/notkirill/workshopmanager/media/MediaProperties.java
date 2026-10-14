package notkirill.workshopmanager.media;

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
        @Name("download-link-ttl") @NotNull @DurationMin(seconds = 1) Duration downloadLinkTtl) {
}
