package notkirill.workshopmanager.ratelimit;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties("application.third-party")
@Validated
public record SteamWebApiProperties(
        @Name("steam-web-api") @Valid ApiProfile profile
) implements ThirdPartyApi {
    public SteamWebApiProperties {
        // From defaults to parsed
        profile = ApiProfile.builder()
                .baseUrl(URI.create("https://api.steampowered.com"))
                .quota(QuotaProfile.builder().maxRequests(100_000).window(Duration.ofDays(1)).build())
                .override(profile)
                .build();
    }

    @Override
    public ApiProfile getProfile() {
        return profile;
    }

    @Override
    public String getBeanName() {
        return "steamWebApi";
    }
}