package notkirill.workshopmanager;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@ConfigurationProperties("application.frontend")
@Validated
public record FrontendProperties(
        @Name("url") @NotNull URI url
) {
}

