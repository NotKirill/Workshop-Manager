package notkirill.workshopmanager.ratelimit;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties("application.third-party")
@Validated
public record SteamStoreApiProperties(
        @Name("steam-store-api") @Valid ApiProfile profile
) implements ThirdPartyApi {
    public SteamStoreApiProperties {
        // From defaults to parsed
        profile = ApiProfile.builder()
                .baseUrl(URI.create("https://store.steampowered.com"))
                .quota(QuotaProfile.builder().maxRequests(150).window(Duration.ofMinutes(5)).build())
                .circuitBreaker(CircuitBreakerProfile.builder()
                                        .minimumCalls(3).windowSize(5).halfOpenCalls(1).openDuration(
                                Duration.ofMinutes(10)).build())
                .override(profile)
                .build();
    }

    @Override
    public ApiProfile getProfile() {
        return profile;
    }

    @Override
    public String getBeanName() {
        return "steamStoreApi";
    }
}