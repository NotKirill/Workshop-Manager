package notkirill.workshopmanager.ratelimit;

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

    @Autowired
    public ApiClientConfig(ThirdPartyRateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
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
                .requestInterceptor(new ThirdPartyInterceptor(api, rateLimitService));
        if (profile.baseUrl() != null) {
            clientBuilder.baseUrl(profile.baseUrl());
        }
        return clientBuilder.build();
    }
}
