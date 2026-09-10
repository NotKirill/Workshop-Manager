package notkirill.workshopmanager;

import com.fasterxml.jackson.annotation.JsonTypeName;
import notkirill.workshopmanager.ott.TokenPayload;
import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    ObjectMapper redisObjectMapper() {
        JsonMapper.Builder mapperBuilder = JsonMapper.builder();

        String mainPackageName = BackendApplication.class.getPackageName();
        String annotationName = JsonTypeName.class.getName();

        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(TokenPayload.class));

        for (BeanDefinition candidate : scanner.findCandidateComponents(mainPackageName)) {
            if (!(candidate instanceof AnnotatedBeanDefinition annotatedCandidate)
                    || !annotatedCandidate.getMetadata().hasAnnotation(annotationName)) {
                continue;
            }

            try {
                Class<?> clazz = Class.forName(candidate.getBeanClassName());
                mapperBuilder.registerSubtypes(clazz);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        return mapperBuilder.build();
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(objectMapper, UserSecurityDto.class)));

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(conf).transactionAware().build();
    }
}
