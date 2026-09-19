package notkirill.workshopmanager.ratelimit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.bind.Name;

import java.time.Duration;

public record CircuitBreakerProfile(
        @Name("minimum-calls") @Positive Integer minimumCalls,
        @Name("failure-rate-percent") @Positive @DecimalMax("100.0") Float failureRatePercent,
        @Name("open-duration") Duration openDuration,
        @Name("window-size") @Positive Integer windowSize,
        @Name("half-open-calls") @Positive Integer halfOpenCalls
) {
    public static CircuitBreakerProfile.Builder builder() {
        return new CircuitBreakerProfile.Builder();
    }

    public CircuitBreakerProfile.Builder toBuilder() {
        return builder().minimumCalls(minimumCalls).failureRatePercent(failureRatePercent).openDuration(openDuration)
                .windowSize(windowSize).halfOpenCalls(halfOpenCalls);
    }

    public CircuitBreaker create(CircuitBreakerRegistry registry, String name) {
        return registry.circuitBreaker(
                name, CircuitBreakerConfig.custom()
                        .minimumNumberOfCalls(minimumCalls)
                        .failureRateThreshold(failureRatePercent)
                        .waitDurationInOpenState(openDuration)
                        .slidingWindowSize(windowSize)
                        .permittedNumberOfCallsInHalfOpenState(halfOpenCalls)
                        .build()
        );
    }

    public static final class Builder {
        private Integer minimumCalls = 10;
        private Float failureRatePercent = 50f;
        private Duration openDuration = Duration.ofSeconds(60);
        private Integer windowSize = 100;
        private Integer halfOpenCalls = 10;

        private Builder() {
        }

        public CircuitBreakerProfile.Builder minimumCalls(Integer minimumCalls) {
            if (minimumCalls != null) {
                this.minimumCalls = minimumCalls;
            }
            return this;
        }

        public CircuitBreakerProfile.Builder failureRatePercent(Float failureRatePercent) {
            if (failureRatePercent != null) {
                this.failureRatePercent = failureRatePercent;
            }
            return this;
        }

        public CircuitBreakerProfile.Builder openDuration(Duration openDuration) {
            if (openDuration != null) {
                this.openDuration = openDuration;
            }
            return this;
        }

        public CircuitBreakerProfile.Builder windowSize(Integer windowSize) {
            if (windowSize != null) {
                this.windowSize = windowSize;
            }
            return this;
        }

        public CircuitBreakerProfile.Builder halfOpenCalls(Integer halfOpenCalls) {
            if (halfOpenCalls != null) {
                this.halfOpenCalls = halfOpenCalls;
            }
            return this;
        }

        public CircuitBreakerProfile.Builder override(CircuitBreakerProfile configured) {
            if (configured == null) {
                return this;
            }
            return minimumCalls(configured.minimumCalls())
                    .failureRatePercent(configured.failureRatePercent())
                    .openDuration(configured.openDuration())
                    .windowSize(configured.windowSize())
                    .halfOpenCalls(configured.halfOpenCalls());
        }

        public CircuitBreakerProfile build() {
            return new CircuitBreakerProfile(minimumCalls, failureRatePercent, openDuration, windowSize, halfOpenCalls);
        }
    }
}
