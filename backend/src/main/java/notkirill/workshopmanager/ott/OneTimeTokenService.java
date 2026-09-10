package notkirill.workshopmanager.ott;

import com.fasterxml.jackson.annotation.JsonTypeName;
import notkirill.workshopmanager.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
public class OneTimeTokenService {
    private static final String OTT_KEY_PREFIX = "ott";

    private final OneTimeTokenProperties ottProperties;

    private final RedisTemplate<String, TokenPayload> redisTemplate;
    private final SecureRandom secureRandom;

    @Autowired
    public OneTimeTokenService(OneTimeTokenProperties ottProperties,
                               RedisTemplate<String, TokenPayload> tokenPayloadRedisTemplate,
                               SecureRandom secureRandom) {
        this.ottProperties = ottProperties;
        this.redisTemplate = tokenPayloadRedisTemplate;
        this.secureRandom = secureRandom;
    }

    public String generateToken(TokenPayload payload) {
        return generateToken(payload, ottProperties.defaultTtl());
    }

    public String generateToken(TokenPayload payload, Duration ttl) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        String redisKey = String.join(":", OTT_KEY_PREFIX, payload.getType(), token);
        redisTemplate.opsForValue().set(redisKey, payload, ttl);
        return token;
    }

    public <T extends TokenPayload> T consumeToken(String token, Class<T> payloadClass) {
        String payloadType = getTokenType(payloadClass);
        String redisKey = String.join(":", OTT_KEY_PREFIX, payloadType, token);
        TokenPayload cachedPayload = redisTemplate.opsForValue().getAndDelete(redisKey);

        if (cachedPayload == null) {
            throw new UnauthorizedException("INVALID_ONE_TIME_TOKEN", "One-time token is unknown, used or expired");
        }

        return payloadClass.cast(cachedPayload);
    }

    private String getTokenType(Class<? extends TokenPayload> payloadClass) {
        JsonTypeName annotation = payloadClass.getAnnotation(JsonTypeName.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Missing @JsonTypeName on " + payloadClass.getName());
        }

        return annotation.value();
    }

}
