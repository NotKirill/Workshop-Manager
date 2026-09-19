package notkirill.workshopmanager.ratelimit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import notkirill.workshopmanager.exception.ServiceUnavailableException;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ThirdPartyInterceptor implements ClientHttpRequestInterceptor {
    private final ThirdPartyApi api;
    private final ThirdPartyRateLimitService rateLimitService;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public ThirdPartyInterceptor(ThirdPartyApi api, ThirdPartyRateLimitService rateLimitService,
                                 CircuitBreaker circuitBreaker, Retry retry) {
        this.api = api;
        this.rateLimitService = rateLimitService;
        this.circuitBreaker = circuitBreaker;
        this.retry = retry;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (!circuitBreaker.tryAcquirePermission()) {
            throw new ServiceUnavailableException(
                    "THIRD_PARTY_UNAVAILABLE",
                    api.getBeanName() + " failed too often recently, calls are paused"
            );
        }

        long start = System.nanoTime();
        ClientHttpResponse response;
        try {
            response = retry.executeCheckedSupplier(() -> send(request, body, execution));
        } catch (RateLimitExceededException | ServiceUnavailableException e) {
            circuitBreaker.releasePermission();
            throw e;
        } catch (IOException | RuntimeException e) {
            circuitBreaker.onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e);
            throw e;
        } catch (Throwable e) {
            // Not thrown by the request; the checked supplier only declares Throwable
            circuitBreaker.onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e);
            throw new IllegalStateException(e);
        }

        if (RetryProfile.isTransient(response)) {
            circuitBreaker.onError(
                    System.nanoTime() - start, TimeUnit.NANOSECONDS,
                    new IOException(api.getBeanName() + " answered HTTP " + response.getStatusCode().value())
            );
        } else {
            circuitBreaker.onSuccess(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
        return response;
    }

    private ClientHttpResponse send(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        rateLimitService.acquire(api);
        return execution.execute(request, body);
    }
}
