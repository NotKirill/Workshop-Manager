package notkirill.workshopmanager.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;

@FunctionalInterface
public interface QuotaBuckets {
    Bucket get(String key, BucketConfiguration configuration);
}
