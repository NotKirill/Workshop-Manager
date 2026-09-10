package notkirill.workshopmanager.user;

import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.dto.UserMapper;
import notkirill.workshopmanager.user.dto.UserResponseDto;
import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFacade {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserFacade(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public List<UserResponseDto> getAllForClient() {
        List<User> entities = userService.getAll();
        return entities.stream().map(userMapper::entityToResponseDto).toList();
    }

    public UserResponseDto getForClientById(Long id) {
        User entity = userService.getById(id);
        return userMapper.entityToResponseDto(entity);
    }

    public UserSecurityDto getForSecurityById(Long id) {
        return UserSecurityDto.of(userService.getById(id));
    }

    // Long TODO maybe to refactor for possible adding cache evict only using userId???
    @Cacheable(value = "user_details", key = "#provider.name() + ':' + #key")
    public UserSecurityDto getForSecurityByProvider(AuthSource provider, String key) {
        return getForSecurityById(userService.getIdByProvider(provider, key));
    }
}
