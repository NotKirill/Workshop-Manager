package notkirill.workshopmanager.ott;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class OneTimeTokenCacheConfig {
    @Bean
    public RedisTemplate<String, TokenPayload> tokenPayloadRedisTemplate(
            RedisConnectionFactory connectionFactory,
            @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {

        RedisTemplate<String, TokenPayload> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        JacksonJsonRedisSerializer<TokenPayload> jsonSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, TokenPayload.class);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}

