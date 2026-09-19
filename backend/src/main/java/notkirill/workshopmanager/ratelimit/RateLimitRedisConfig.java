package notkirill.workshopmanager.ratelimit;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RateLimitRedisConfig {
    @Bean
    public StatefulRedisConnection<String, byte[]> rateLimitRedisConnection(LettuceConnectionFactory factory) {
        if (!(factory.getRequiredNativeClient() instanceof RedisClient client)) {
            throw new IllegalStateException("Rate limits need a standalone Redis (no cluster client)");
        }
        return client.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    public ProxyManager<String> rateLimitBuckets(StatefulRedisConnection<String, byte[]> connection) {
        return Bucket4jLettuce.casBasedBuilder(connection)
                .expirationAfterWrite(ExpirationAfterWriteStrategy
                                              .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(10)))
                .build();
    }

    @Bean
    public QuotaBuckets quotaBuckets(ProxyManager<String> rateLimitBuckets) {
        return (key, configuration) -> rateLimitBuckets.builder().build(key, () -> configuration);
    }
}
