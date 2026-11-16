package notkirill.workshopmanager.ratelimit;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class ApiClientConfig {
    public static final String STEAM_WEB_API_CLIENT = "steamWebApiRestClient";
    public static final String STEAM_STORE_API_CLIENT = "steamStoreApiRestClient";
    public static final String STEAM_COMMUNITY_API_CLIENT = "steamCommunityApiRestClient";

    private final ThirdPartyRateLimitService rateLimitService;
    private final ObservationRegistry observationRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @Autowired
    public ApiClientConfig(ThirdPartyRateLimitService rateLimitService, ObservationRegistry observationRegistry,
                           CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.rateLimitService = rateLimitService;
        this.observationRegistry = observationRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @Bean(STEAM_WEB_API_CLIENT)
    public RestClient steamWebApiClient(SteamWebApiProperties api) {
        return create(api);
    }

    @Bean(STEAM_STORE_API_CLIENT)
    public RestClient steamStoreApiClient(SteamStoreApiProperties api) {
        return create(api);
    }

    @Bean(STEAM_COMMUNITY_API_CLIENT)
    public RestClient steamCommunityApiClient(SteamCommunityApiProperties api) {
        return create(api);
    }

    private RestClient create(ThirdPartyApi api) {
        ApiProfile profile = api.getProfile();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(profile.connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(profile.readTimeout());

        RestClient.Builder clientBuilder = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "WorkshopManager/1.0")
                .observationRegistry(observationRegistry)
                .observationConvention(new PathObservationConvention())
                .requestInterceptor(new ThirdPartyInterceptor(
                        api, rateLimitService,
                        profile.circuitBreakerProfile().create(circuitBreakerRegistry, api.getBeanName()),
                        profile.retry().create(retryRegistry, api.getBeanName())
                ));
        if (profile.baseUrl() != null) {
            clientBuilder.baseUrl(profile.baseUrl());
        }
        return clientBuilder.build();
    }
}
