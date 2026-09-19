package notkirill.workshopmanager.ratelimit;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import notkirill.workshopmanager.exception.ServiceUnavailableException;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ThirdPartyInterceptorTest {
    private static final String URL = "https://steam.test/x";
    private static final RetryProfile RETRY = new RetryProfile(3, Duration.ofMillis(1), Duration.ofMillis(1));

    private ThirdPartyRateLimitService rateLimitService;
    private io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;
    private MockRestServiceServer server;
    private RestClient client;

    private void setUp(int quota, RetryProfile retry) {
        ThirdPartyApi api = new ThirdPartyRateLimitServiceTest.TestApi(
                "testApi", ApiProfile.builder()
                .quota(QuotaProfile.builder().maxRequests(quota).window(Duration.ofDays(1)).build())
                .circuitBreaker(
                        CircuitBreakerProfile.builder().minimumCalls(4).openDuration(Duration.ofMinutes(1)).build())
                .retry(retry)
                .build()
        );
        circuitBreaker = api.getProfile().circuitBreakerProfile()
                .create(CircuitBreakerRegistry.ofDefaults(), api.getBeanName());
        rateLimitService = new ThirdPartyRateLimitService(
                ThirdPartyRateLimitServiceTest.localBuckets(new HashMap<>()), List.of(api));
        RestClient.Builder builder = RestClient.builder().requestInterceptor(new ThirdPartyInterceptor(
                api, rateLimitService, circuitBreaker,
                api.getProfile().retry().create(RetryRegistry.ofDefaults(), api.getBeanName())
        ));
        server = MockRestServiceServer.bindTo(builder).build();
        client = builder.build();
    }

    @Test
    void transientFailureIsRetriedAndEveryAttemptTakesQuota() {
        setUp(10, RETRY);
        server.expect(once(), requestTo(URL)).andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        server.expect(once(), requestTo(URL)).andRespond(withException(new SocketTimeoutException("read timed out")));
        server.expect(once(), requestTo(URL)).andRespond(withSuccess("ok", null));

        assertThat(get()).isEqualTo("ok");
        server.verify();
        assertThat(rateLimitService.getBucketStatuses().getFirst().availableTokens()).isEqualTo(7);
        assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(1);
    }

    @Test
    void callFailingAfterAllAttemptsIsOneFailureForTheCircuit() {
        setUp(10, RETRY);
        server.expect(times(3), requestTo(URL)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(this::get).isInstanceOf(HttpServerErrorException.class);
        server.verify();
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
    }

    @Test
    void timeoutsInEveryAttemptReachTheCallerAsUsual() {
        setUp(10, RETRY);
        server.expect(times(3), requestTo(URL)).andRespond(withException(new SocketTimeoutException("timeout")));

        assertThatThrownBy(this::get).isInstanceOf(ResourceAccessException.class);
        server.verify();
    }

    @Test
    void clientErrorsAreAnswersNeitherRetriedNorFailures() {
        setUp(10, RETRY);
        server.expect(once(), requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(this::get).isInstanceOf(HttpClientErrorException.class);
        server.verify();
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void singleAttemptProfileSendsOnce() {
        setUp(10, RetryProfile.builder().maxAttempts(1).build());
        server.expect(once(), requestTo(URL)).andRespond(withStatus(HttpStatus.BAD_GATEWAY));

        assertThatThrownBy(this::get).isInstanceOf(HttpServerErrorException.class);
        server.verify();
    }

    @Test
    void repeatedFailuresOpenTheCircuitAndThenNothingIsSent() {
        setUp(100, RetryProfile.builder().maxAttempts(1).build());
        server.expect(times(4), requestTo(URL)).andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        for (int i = 0; i < 4; i++) {
            assertThatThrownBy(this::get).isInstanceOf(HttpServerErrorException.class);
        }
        assertThat(circuitBreaker.getState()).isEqualTo(
                io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN);

        assertThatThrownBy(this::get).isInstanceOfSatisfying(
                ServiceUnavailableException.class,
                e -> assertThat(e.getCode()).isEqualTo("THIRD_PARTY_UNAVAILABLE")
        );
        server.verify();
    }

    @Test
    void quotaRefusalIsNotRetriedAndSendsNothing() {
        setUp(1, RETRY);
        server.expect(once(), requestTo(URL)).andRespond(withSuccess());
        get();

        assertThatThrownBy(this::get).isExactlyInstanceOf(RateLimitExceededException.class);
        server.verify();
        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isZero();
    }

    private String get() {
        return client.get().uri(URL).retrieve().body(String.class);
    }
}
