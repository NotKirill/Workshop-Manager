package notkirill.workshopmanager.user;

import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService {
    private final UserService userService;
    private final UserSecurityCache securityCache;

    @Autowired
    public UserSecurityService(UserService userService, UserSecurityCache securityCache) {
        this.userService = userService;
        this.securityCache = securityCache;
    }

    public UserSecurityDto getForSecurityById(Long id) {
        UserSecurityDto cached = securityCache.get(id);
        if (cached != null) {
            return cached;
        }
        UserSecurityDto dto = UserSecurityDto.of(userService.getById(id));
        securityCache.put(id, dto);
        return dto;
    }

    public UserSecurityDto getForSecurityByProvider(AuthSource provider, String key) {
        return getForSecurityById(userService.getIdByProvider(provider, key));
    }
}
