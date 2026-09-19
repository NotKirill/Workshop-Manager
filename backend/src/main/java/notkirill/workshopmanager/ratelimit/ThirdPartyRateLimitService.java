package notkirill.workshopmanager.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import notkirill.workshopmanager.exception.ServiceUnavailableException;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ThirdPartyRateLimitService {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyRateLimitService.class);
    private static final String QUOTA_PREFIX = "rl:quota";

    private final QuotaBuckets buckets;
    private final List<ThirdPartyApi> apis;

    @Autowired
    public ThirdPartyRateLimitService(QuotaBuckets buckets, List<ThirdPartyApi> apis) {
        this.buckets = buckets;
        this.apis = apis;
    }

    public void acquire(ThirdPartyApi api) {
        QuotaProfile quotaProfile = api.getProfile().quotaProfile();
        Bucket bucket;
        ConsumptionProbe probe;
        try {
            bucket = getBucket(api);
            probe = bucket.tryConsumeAndReturnRemaining(1);
        } catch (RuntimeException e) {
            log.warn("Rate limiter of {} unavailable: {}", api.getBeanName(), e.getMessage());
            throw new ServiceUnavailableException(
                    "RATE_LIMITER_UNAVAILABLE", "Quota of " + api.getBeanName() + " cannot be checked");
        }
        if (!probe.isConsumed()) {
            throw new RateLimitExceededException(
                    "THIRD_PARTY_RATE_LIMIT", "Quota of " + api.getBeanName()
                    + " is used up for " + Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds() + " s"
            );
        }
        if (CallPriority.current() == CallPriority.BACKGROUND
                && probe.getRemainingTokens() < quotaProfile.reservedForInteractive()) {
            bucket.addTokens(1);
            throw new RateLimitExceededException(
                    "THIRD_PARTY_RATE_LIMIT", "Only the reserved quota of " + api.getBeanName() + " is left");
        }
    }

    public List<Status> getBucketStatuses() {
        return apis.stream().map(this::getBucketStatus).toList();
    }

    public Status getBucketStatus(ThirdPartyApi api) {
        QuotaProfile quotaProfile = api.getProfile().quotaProfile();
        Long available;
        try {
            available = getBucket(api).getAvailableTokens();
        } catch (RuntimeException e) {
            log.warn("Rate limiter of {} unavailable: {}", api.getBeanName(), e.getMessage());
            available = null;
        }
        return new Status(
                api.getBeanName(), quotaProfile.maxRequests(), quotaProfile.window(), available,
                quotaProfile.reservedForInteractive()
        );
    }

    private Bucket getBucket(ThirdPartyApi api) {
        QuotaProfile quotaProfile = api.getProfile().quotaProfile();
        String key = String.join(
                ":", QUOTA_PREFIX, api.getBeanName(),
                quotaProfile.maxRequests() + "-" + quotaProfile.window().toSeconds()
        );
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(bandwidth -> bandwidth.capacity(quotaProfile.maxRequests())
                        .refillGreedy(quotaProfile.maxRequests(), quotaProfile.window()))
                .build();
        return buckets.get(key, configuration);
    }

    public record Status(String api, long maxRequests, Duration window, Long availableTokens,
                         long reservedForInteractive) {
    }
}
