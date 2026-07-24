package notkirill.workshopmanager.user.dto;

import notkirill.workshopmanager.user.User;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto entityToResponseDto(User entity) {
        return new UserResponseDto(
                entity.getId(),
                entity.getTimeCreated(),
                entity.getName()
        );
    }

    public UserSecurityDto entityToSecurityDto(User entity, PermissionEnum[] permissions) {
        return new UserSecurityDto(
                entity.getId(),
                entity.getPasswordHash(),
                entity.getTimeCreated(),
                entity.getName(),
                permissions,
                entity.isEnabled(),
                entity.isAccountNonLocked()
        );
    }

    public void updateEntityFromEntity(User oldEntity, User newEntity) {
        oldEntity.setPasswordHash(newEntity.getPasswordHash());
        oldEntity.setName(newEntity.getName());
        oldEntity.setPermissionIds(newEntity.getPermissionIds());
        oldEntity.setEnabled(newEntity.isEnabled());
        oldEntity.setAccountNonLocked(newEntity.isAccountNonLocked());
    }
}
