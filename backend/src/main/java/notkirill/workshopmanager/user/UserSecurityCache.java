package notkirill.workshopmanager.user;

import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserSecurityCache {
    public static final String NAME = "user_details";
    private static final Logger log = LoggerFactory.getLogger(UserSecurityCache.class);

    private final Cache cache;

    @Autowired
    public UserSecurityCache(CacheManager cacheManager) {
        this.cache = Objects.requireNonNull(cacheManager.getCache(NAME));
    }

    public UserSecurityDto get(long userId) {
        try {
            return cache.get(userId, UserSecurityDto.class);
        } catch (RuntimeException e) {
            log.warn("User security cache unavailable, reading user {} from the database: {}", userId,
                    e.getMessage());
            return null;
        }
    }

    public void put(long userId, UserSecurityDto dto) {
        try {
            cache.put(userId, dto);
        } catch (RuntimeException e) {
            log.debug("User {} not cached: {}", userId, e.getMessage());
        }
    }

    public void evict(long userId) {
        cache.evict(userId);
    }
}
