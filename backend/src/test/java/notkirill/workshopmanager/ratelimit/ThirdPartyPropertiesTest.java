package notkirill.workshopmanager.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ThirdPartyPropertiesTest {
    private static final Map<String, String> CONFIGURATION = Map.of(
            "application.third-party.steam-web-api.read-timeout", "5s",
            "application.third-party.steam-store-api.quota.max-requests", "10",
            "application.third-party.steam-store-api.circuit-breaker.open-duration", "2m",
            "application.third-party.steam-community-api.base-url", "http://localhost:9",
            "application.third-party.steam-community-api.circuit-breaker.open-duration", "20s",
            "application.third-party.steam-community-api.retry.max-wait", "5s"
    );

    private static ThirdPartyApi[] bind(Map<String, String> configuration) {
        Binder binder = new Binder(new MapConfigurationPropertySource(configuration));
        return new ThirdPartyApi[]{
                bind(binder, SteamWebApiProperties.class),
                bind(binder, SteamStoreApiProperties.class),
                bind(binder, SteamCommunityApiProperties.class)
        };
    }

    private static <T> T bind(Binder binder, Class<T> type) {
        return binder.bindOrCreate("application.third-party", type);
    }

    @Test
    void unconfiguredApisGetCompleteDefaults() {
        for (ThirdPartyApi api : bind(Map.of())) {
            ApiProfile profile = api.getProfile();
            assertThat(profile.quotaProfile().maxRequests()).isPositive();
            assertThat(profile.quotaProfile().window()).isPositive();
            assertThat(profile.quotaProfile().backgroundShare()).isNotNull();
            assertThat(profile.connectTimeout()).isPositive();
            assertThat(profile.readTimeout()).isPositive();
            assertThat(profile.circuitBreakerProfile().openDuration()).isPositive();
            assertThat(profile.retry().maxAttempts()).isPositive();
            // Must not throw
            profile.quotaProfile().reservedForInteractive();
        }
    }

    @Test
    void aConfiguredValueReplacesOnlyItself() {
        ThirdPartyApi[] apis = bind(CONFIGURATION);
        ApiProfile web = apis[0].getProfile();
        ApiProfile store = apis[1].getProfile();
        ApiProfile community = apis[2].getProfile();

        assertThat(web.readTimeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(web.connectTimeout()).isEqualTo(Duration.ofSeconds(20));
        assertThat(web.baseUrl()).isEqualTo(URI.create("https://api.steampowered.com"));
        assertThat(store.quotaProfile().maxRequests()).isEqualTo(10);
        assertThat(store.quotaProfile().window()).isEqualTo(Duration.ofMinutes(5));
        assertThat(store.circuitBreakerProfile().openDuration()).isEqualTo(Duration.ofMinutes(2));
        assertThat(store.circuitBreakerProfile().minimumCalls()).isEqualTo(3);
        assertThat(store.circuitBreakerProfile().windowSize()).isEqualTo(5);
        assertThat(store.circuitBreakerProfile().halfOpenCalls()).isEqualTo(1);
        assertThat(web.circuitBreakerProfile().windowSize()).isEqualTo(100);
        assertThat(web.circuitBreakerProfile().halfOpenCalls()).isEqualTo(10);
        assertThat(community.baseUrl()).isEqualTo(URI.create("http://localhost:9"));
        assertThat(community.circuitBreakerProfile().openDuration()).isEqualTo(Duration.ofSeconds(20));
        assertThat(community.circuitBreakerProfile().minimumCalls()).isEqualTo(10);
        assertThat(web.retry().maxAttempts()).isEqualTo(3);
        assertThat(community.retry().maxAttempts()).isEqualTo(1);
        assertThat(community.retry().maxWait()).isEqualTo(Duration.ofSeconds(5));
        assertThat(community.retry().initialWait()).isEqualTo(Duration.ofMillis(500));
    }
}
