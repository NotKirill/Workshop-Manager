package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserGroup;
import notkirill.workshopmanager.models.dto.UserGroupRequest;
import notkirill.workshopmanager.models.dto.UserGroupResponse;

public class UserGroupMapper {
    public UserGroupResponse fromEntity(UserGroup entity) {
        return new UserGroupResponse(entity.getId(),
                entity.getUserId(),
                entity.getGameId(),
                entity.getName());


    }

    public UserGroup toEntity(UserGroupRequest request) {
        return new UserGroup(request.userId(),
                request.gameId(),
                request.name());
    }
}
