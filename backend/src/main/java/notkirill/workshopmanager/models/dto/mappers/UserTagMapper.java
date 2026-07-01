package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserTag;
import notkirill.workshopmanager.models.dto.UserTagRequest;
import notkirill.workshopmanager.models.dto.UserTagResponse;

public class UserTagMapper {
    public UserTagResponse entityToResponse(UserTag entity) {
        return new UserTagResponse(entity.getId(),
                entity.getDisplayName(),
                entity.getGameId());
    }

    public UserTag requestToEntity(UserTagRequest request) {
        return new UserTag(request.displayName(),
                request.gameId());
    }

    public void updateEntityFromRequest(UserTagRequest request, UserTag entity) {
        entity.setDisplayName(request.displayName());
        entity.setGameId(request.gameId());
    }
}
