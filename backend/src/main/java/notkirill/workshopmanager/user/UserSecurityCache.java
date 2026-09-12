package notkirill.workshopmanager.user;

import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserSecurityCache {
    public static final String NAME = "user_details";

    private final Cache cache;

    @Autowired
    public UserSecurityCache(CacheManager cacheManager) {
        this.cache = Objects.requireNonNull(cacheManager.getCache(NAME));
    }

    public UserSecurityDto get(long userId) {
        return cache.get(userId, UserSecurityDto.class);
    }

    public void put(long userId, UserSecurityDto dto) {
        cache.put(userId, dto);
    }

    public void evict(long userId) {
        cache.evict(userId);
    }
}
