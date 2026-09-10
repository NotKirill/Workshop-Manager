package notkirill.workshopmanager.ott;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("application.one-time-code")
@Validated
public record OneTimeTokenProperties(
        @Name("default-ttl") Duration defaultTtl
) {
    public OneTimeTokenProperties {
        if (defaultTtl == null) {
            defaultTtl = Duration.ofMinutes(1);
        }
    }
}

