package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserTag;
import notkirill.workshopmanager.models.dto.UserTagRequest;
import notkirill.workshopmanager.models.dto.UserTagResponse;

public class UserTagMapper {
    public UserTagResponse fromEntity(UserTag entity) {
        return new UserTagResponse(entity.getId(),
                entity.getDisplayName(),
                entity.getGameId());
    }

    public UserTag toEntity(UserTagRequest request) {
        return new UserTag(request.displayName(),
                request.gameId());
    }
}
