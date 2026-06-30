package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.User;
import notkirill.workshopmanager.models.dto.UserRequest;
import notkirill.workshopmanager.models.dto.UserResponse;

public class UserMapper {
    public UserResponse fromEntity(User entity) {
        return new UserResponse(entity.getId(),
                entity.getLogin(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getTimeCreated(),
                entity.getName(),
                entity.getPermissionsIds());


    }

    public User toEntity(UserRequest request) {
        return new User(request.login(),
                request.email(),
                request.passwordHash(),
                request.timeCreated(),
                request.name(),
                request.permissionsIds());
    }
}
