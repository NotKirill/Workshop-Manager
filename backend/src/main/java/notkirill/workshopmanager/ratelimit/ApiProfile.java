package notkirill.workshopmanager.ratelimit;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.bind.Name;

import java.net.URI;
import java.time.Duration;

public record ApiProfile(
        @Name("base-url") URI baseUrl,
        @Name("connect-timeout") Duration connectTimeout,
        @Name("read-timeout") Duration readTimeout,
        @Name("quota") @Valid QuotaProfile quotaProfile,
        @Name("circuit-breaker") @Valid CircuitBreakerProfile circuitBreakerProfile,
        @Name("retry") @Valid RetryProfile retry
) {
    public static ApiProfile.Builder builder() {
        return new ApiProfile.Builder();
    }

    public static final class Builder {
        private URI baseUrl;
        private Duration connectTimeout = Duration.ofSeconds(20);
        private Duration readTimeout = Duration.ofSeconds(20);
        private QuotaProfile quota = new QuotaProfile(null, null, null);
        private CircuitBreakerProfile circuitBreakerProfile = CircuitBreakerProfile.builder().build();
        private RetryProfile retry = RetryProfile.builder().build();

        private Builder() {
        }

        public ApiProfile.Builder baseUrl(URI baseUrl) {
            if (baseUrl != null) {
                this.baseUrl = baseUrl;
            }
            return this;
        }

        public ApiProfile.Builder connectTimeout(Duration connectTimeout) {
            if (connectTimeout != null) {
                this.connectTimeout = connectTimeout;
            }
            return this;
        }

        public ApiProfile.Builder readTimeout(Duration readTimeout) {
            if (readTimeout != null) {
                this.readTimeout = readTimeout;
            }
            return this;
        }

        public ApiProfile.Builder quota(QuotaProfile quota) {
            if (quota != null) {
                this.quota = quota;
            }
            return this;
        }

        public ApiProfile.Builder circuitBreaker(
                CircuitBreakerProfile circuitBreakerProfile) {
            if (circuitBreakerProfile != null) {
                this.circuitBreakerProfile = circuitBreakerProfile;
            }
            return this;
        }

        public ApiProfile.Builder retry(RetryProfile retry) {
            if (retry != null) {
                this.retry = retry;
            }
            return this;
        }

        public ApiProfile.Builder override(ApiProfile configured) {
            if (configured == null) {
                return this;
            }
            return baseUrl(configured.baseUrl())
                    .connectTimeout(configured.connectTimeout())
                    .readTimeout(configured.readTimeout())
                    .quota(configured.quotaProfile())
                    .circuitBreaker(configured.circuitBreakerProfile())
                    .retry(configured.retry());
        }

        public ApiProfile build() {
            return new ApiProfile(baseUrl, connectTimeout, readTimeout, quota, circuitBreakerProfile, retry);
        }
    }
}