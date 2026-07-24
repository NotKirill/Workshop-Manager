package notkirill.workshopmanager.user;

import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.dto.UserMapper;
import notkirill.workshopmanager.user.dto.UserResponseDto;
import notkirill.workshopmanager.user.dto.UserSecurityDto;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import notkirill.workshopmanager.user.permission.PermissionService;
import notkirill.workshopmanager.user.permission.dto.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFacade {
    private final UserService userService;
    private final PermissionService permissionService;
    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;

    @Autowired
    public UserFacade(UserService userService, PermissionService permissionService, UserMapper userMapper,
                      PermissionMapper permissionMapper) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.userMapper = userMapper;
        this.permissionMapper = permissionMapper;
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
        User entity = userService.getById(id);
        return userMapper.entityToSecurityDto(entity, getUserPermissions(entity));
    }

    // Long TODO maybe to refactor for possible adding cache evict only using userId???
    @Cacheable(value = "user_details", key = "#provider.name() + ':' + #key")
    public UserSecurityDto getForSecurityByProvider(AuthSource provider, String key) {
        User entity = userService.getByProvider(provider, key);
        return userMapper.entityToSecurityDto(entity, getUserPermissions(entity));
    }

    private PermissionEnum[] getUserPermissions(User user) {
        return permissionService.getAllByIds(user.getPermissionIds())
                .stream().map(permissionMapper::entityToEnum)
                .toArray(PermissionEnum[]::new);
    }
}
