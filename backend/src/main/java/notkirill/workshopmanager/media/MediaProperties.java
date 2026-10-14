package notkirill.workshopmanager.media;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("application.media")
@Validated
public record MediaProperties(
        @Name("max-image-size") @NotNull DataSize maxImageSize) {
}
