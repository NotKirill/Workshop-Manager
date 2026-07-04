package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.User;
import notkirill.workshopmanager.models.dto.UserRequest;
import notkirill.workshopmanager.models.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse entityToResponse(User entity) {
        return new UserResponse(entity.getId(),
                entity.getLogin(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getTimeCreated(),
                entity.getName(),
                entity.getPermissionsIds());
    }

    public User requestToEntity(UserRequest request) {
        return new User(request.login(),
                request.email(),
                request.passwordHash(),
                request.timeCreated(),
                request.name(),
                request.permissionsIds());
    }

    public void updateEntityFromRequest(UserRequest request, User entity) {
        entity.setLogin(request.login());
        entity.setEmail(request.email());
        entity.setPasswordHash(request.passwordHash());
        entity.setTimeCreated(request.timeCreated());
        entity.setName(request.name());
        entity.setPermissionsIds(request.permissionsIds());
    }
}
