package notkirill.workshopmanager.ratelimit;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;

public record RetryProfile(
        @Name("max-attempts") @Positive Integer maxAttempts,
        @Name("initial-wait") Duration initialWait,
        @Name("max-wait") Duration maxWait
) {
    public static RetryProfile.Builder builder() {
        return new RetryProfile.Builder();
    }

    static boolean isTransient(ClientHttpResponse response) {
        try {
            int status = response.getStatusCode().value();
            return status == 429 || status >= 500;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public RetryProfile.Builder toBuilder() {
        return builder().maxAttempts(maxAttempts).initialWait(initialWait).maxWait(maxWait);
    }

    public Retry create(RetryRegistry registry, String name) {
        return registry.retry(
                name, RetryConfig.<ClientHttpResponse>custom()
                        .maxAttempts(maxAttempts)
                        .intervalFunction(IntervalFunction.ofExponentialBackoff(initialWait, 2.0, maxWait))
                        .retryExceptions(IOException.class)
                        .retryOnResult(RetryProfile::isTransient)
                        // The answer of a failed attempt is thrown away: close it, or its connection stays open
                        .consumeResultBeforeRetryAttempt((attempt, response) -> response.close())
                        .build()
        );
    }

    public static final class Builder {
        private Integer maxAttempts = 3;
        private Duration initialWait = Duration.ofMillis(500);
        private Duration maxWait = Duration.ofSeconds(2);

        private Builder() {
        }

        public RetryProfile.Builder maxAttempts(Integer maxAttempts) {
            if (maxAttempts != null) {
                this.maxAttempts = maxAttempts;
            }
            return this;
        }

        public RetryProfile.Builder initialWait(Duration initialWait) {
            if (initialWait != null) {
                this.initialWait = initialWait;
            }
            return this;
        }

        public RetryProfile.Builder maxWait(Duration maxWait) {
            if (maxWait != null) {
                this.maxWait = maxWait;
            }
            return this;
        }

        public RetryProfile.Builder override(RetryProfile configured) {
            if (configured == null) {
                return this;
            }
            return maxAttempts(configured.maxAttempts())
                    .initialWait(configured.initialWait())
                    .maxWait(configured.maxWait());
        }

        public RetryProfile build() {
            return new RetryProfile(
                    maxAttempts, initialWait, maxWait.compareTo(initialWait) < 0 ? initialWait : maxWait);
        }
    }
}
