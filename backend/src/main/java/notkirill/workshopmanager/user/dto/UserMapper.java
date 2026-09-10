package notkirill.workshopmanager.user.dto;

import notkirill.workshopmanager.user.User;
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
}
