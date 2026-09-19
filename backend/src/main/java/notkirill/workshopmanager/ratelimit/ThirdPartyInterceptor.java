package notkirill.workshopmanager.ratelimit;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class ThirdPartyInterceptor implements ClientHttpRequestInterceptor {
    private final ThirdPartyApi api;
    private final ThirdPartyRateLimitService rateLimitService;

    public ThirdPartyInterceptor(ThirdPartyApi api, ThirdPartyRateLimitService rateLimitService) {
        this.api = api;
        this.rateLimitService = rateLimitService;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {
        rateLimitService.acquire(api);
        return execution.execute(request, body);
    }
}
