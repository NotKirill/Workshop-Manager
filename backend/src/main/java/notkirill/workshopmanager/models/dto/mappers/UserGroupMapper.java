package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserGroup;
import notkirill.workshopmanager.models.dto.UserGroupRequest;
import notkirill.workshopmanager.models.dto.UserGroupResponse;

public class UserGroupMapper {
    public UserGroupResponse entityToResponse(UserGroup entity) {
        return new UserGroupResponse(entity.getId(),
                entity.getUserId(),
                entity.getGameId(),
                entity.getName());
    }

    public UserGroup requestToEntity(UserGroupRequest request) {
        return new UserGroup(request.userId(),
                request.gameId(),
                request.name());
    }

    public void updateEntityFromRequest(UserGroupRequest request, UserGroup entity) {
        entity.setUserId(request.userId());
        entity.setGameId(request.gameId());
        entity.setName(request.name());
    }
}
