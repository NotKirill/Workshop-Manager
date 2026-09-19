package notkirill.workshopmanager.ratelimit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CircuitBreakerProfileTest {
    private CircuitBreaker circuit;

    @BeforeEach
    void setUp() {
        CircuitBreakerProfile profile = CircuitBreakerProfile.builder()
                .minimumCalls(3)
                .windowSize(5)
                .failureRatePercent(50f)
                .halfOpenCalls(1)
                .openDuration(Duration.ofMinutes(10))
                .build();
        circuit = profile.create(CircuitBreakerRegistry.ofDefaults(), "test");
    }

    @Test
    void opensOnceMinimumCallsHaveFailed() {
        fail();
        fail();
        assertThat(circuit.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        fail();
        assertThat(circuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        assertThat(circuit.tryAcquirePermission()).isFalse();
    }

    @Test
    void onlyTheLastWindowSizeCallsCount() {
        for (int i = 0; i < 20; i++) {
            succeed();
        }
        fail();
        fail();
        assertThat(circuit.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        fail();
        assertThat(circuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void halfOpenCallsDecideAfterTheOpenDuration() {
        openCircuit();
        circuit.transitionToHalfOpenState();
        succeed();
        assertThat(circuit.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        openCircuit();
        circuit.transitionToHalfOpenState();
        fail();
        assertThat(circuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    private void openCircuit() {
        circuit.reset();
        fail();
        fail();
        fail();
    }

    private void succeed() {
        assertThat(circuit.tryAcquirePermission()).isTrue();
        circuit.onSuccess(0, TimeUnit.MILLISECONDS);
    }

    private void fail() {
        assertThat(circuit.tryAcquirePermission()).isTrue();
        circuit.onError(0, TimeUnit.MILLISECONDS, new IOException("API down"));
    }
}
