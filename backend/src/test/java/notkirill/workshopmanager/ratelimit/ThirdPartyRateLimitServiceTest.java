package notkirill.workshopmanager.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThirdPartyRateLimitServiceTest {
    // 10 calls per day, 70% for scheduled jobs: 3 calls are reserved
    static final ThirdPartyApi WEB = new TestApi(
            "webApi", ApiProfile.builder()
            .quota(QuotaProfile.builder().maxRequests(10).window(Duration.ofDays(1)).backgroundShare(0.7).build())
            .build()
    );
    static final ThirdPartyApi STORE = new TestApi(
            "storeApi", ApiProfile.builder()
            .quota(QuotaProfile.builder().maxRequests(10).window(Duration.ofDays(1)).build())
            .build()
    );

    private final Map<String, Bucket> buckets = new HashMap<>();
    private ThirdPartyRateLimitService service;
    private SimpleMeterRegistry meterRegistry;

    static QuotaBuckets localBuckets(Map<String, Bucket> buckets) {
        return (key, configuration) -> buckets.computeIfAbsent(
                key, k -> {
                    LocalBucketBuilder builder = Bucket.builder();
                    for (Bandwidth bandwidth : configuration.getBandwidths()) {
                        builder.addLimit(bandwidth);
                    }
                    return builder.build();
                }
        );
    }

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new ThirdPartyRateLimitService(localBuckets(buckets), List.of(WEB, STORE), meterRegistry);
    }

    @Test
    void interactiveCallsMayUseTheWholeQuota() {
        for (int i = 0; i < 10; i++) {
            service.acquire(WEB);
        }
        assertThatThrownBy(() -> service.acquire(WEB))
                .isInstanceOfSatisfying(
                        RateLimitExceededException.class,
                        e -> assertThat(e.getCode()).isEqualTo("THIRD_PARTY_RATE_LIMIT")
                );
    }

    @Test
    void scheduledJobsLeaveTheReserveForInteractiveCalls() {
        CallPriority.BACKGROUND.run(() -> {
            for (int i = 0; i < 7; i++) {
                service.acquire(WEB);
            }
            assertThatThrownBy(() -> service.acquire(WEB)).isInstanceOf(RateLimitExceededException.class);
        });
        for (int i = 0; i < 3; i++) {
            service.acquire(WEB);
        }
        assertThat(service.getBucketStatuses().getFirst().availableTokens()).isZero();
    }

    @Test
    void refusalsAreCountedByReason() {
        CallPriority.BACKGROUND.run(() -> {
            for (int i = 0; i < 7; i++) {
                service.acquire(WEB);
            }
            assertThatThrownBy(() -> service.acquire(WEB)).isInstanceOf(RateLimitExceededException.class);
        });
        for (int i = 0; i < 3; i++) {
            service.acquire(WEB);
        }
        assertThatThrownBy(() -> service.acquire(WEB)).isInstanceOf(RateLimitExceededException.class);

        assertThat(refusals("RESERVE")).isEqualTo(1);
        assertThat(refusals("QUOTA")).isEqualTo(1);
    }

    @Test
    void apisHaveSeparateQuotas() {
        for (int i = 0; i < 10; i++) {
            service.acquire(WEB);
        }
        service.acquire(STORE);
    }

    @Test
    void getBucketStatusesListEveryApi() {
        assertThat(service.getBucketStatuses()).extracting(ThirdPartyRateLimitService.Status::api)
                .containsExactly("webApi", "storeApi");
        assertThat(service.getBucketStatuses().getFirst().reservedForInteractive()).isEqualTo(3);
    }

    private double refusals(String reason) {
        return meterRegistry.counter("workshop.ratelimit.refusals", "api", "webApi", "reason", reason).count();
    }

    record TestApi(String beanName, ApiProfile profile) implements ThirdPartyApi {
        @Override
        public ApiProfile getProfile() {
            return profile;
        }

        @Override
        public String getBeanName() {
            return beanName;
        }
    }
}
