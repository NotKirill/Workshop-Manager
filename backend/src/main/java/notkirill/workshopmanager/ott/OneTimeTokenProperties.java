package notkirill.workshopmanager.ott;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("application.one-time-code")
@Validated
public record OneTimeTokenProperties(
        @Name("ttl") Duration ttl
) {
    public OneTimeTokenProperties {
        if (ttl == null) {
            ttl = Duration.ofMinutes(1);
        }
    }
}

