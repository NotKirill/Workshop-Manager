package notkirill.workshopmanager.game.lookup;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

import static notkirill.workshopmanager.game.lookup.GameLookupProperties.CONF_PATH;

@ConfigurationProperties(CONF_PATH)
@Validated
public record GameLookupProperties(
        @Name("enabled") Boolean enabled,
        @Name("interval") Duration interval,
        @Name("max-per-run") Integer maxPerRun
) {
    public static final String CONF_PATH = "application.games.lookup";

    public GameLookupProperties {
        if (enabled == null) {
            enabled = true;
        }
        if (interval == null || interval.isZero() || interval.isNegative()) {
            interval = Duration.ofMinutes(1);
        }
        if (maxPerRun == null || maxPerRun <= 0) {
            maxPerRun = 15;
        }
    }
}
