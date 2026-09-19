package notkirill.workshopmanager.ratelimit;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("application.third-party")
@Validated
public record SteamCommunityApiProperties(
        @Name("steam-community-api") @Valid ApiProfile profile
) implements ThirdPartyApi {
    public SteamCommunityApiProperties {
        // From defaults to parsed
        // TODO check why background share 1.0
        // TODO so questionable
        profile = ApiProfile.builder()
                .quota(QuotaProfile.builder().maxRequests(60).window(Duration.ofMinutes(1)).backgroundShare(
                        1.0).build())
                .retry(RetryProfile.builder().maxAttempts(1).build())
                .override(profile)
                .build();
    }

    @Override
    public ApiProfile getProfile() {
        return profile;
    }

    @Override
    public String getBeanName() {
        return "steamCommunityApi";
    }
}
