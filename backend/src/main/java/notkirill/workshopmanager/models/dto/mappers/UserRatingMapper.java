package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserRating;
import notkirill.workshopmanager.models.dto.UserRatingRequest;
import notkirill.workshopmanager.models.dto.UserRatingResponse;

public class UserRatingMapper {
    public UserRatingResponse entityToResponse(UserRating entity) {
        return new UserRatingResponse(entity.getUserId(),
                entity.getItemPersonalMetadataId(),
                entity.getScore());
    }

    public UserRating requestToEntity(UserRatingRequest request) {
        return new UserRating(request.userId(),
                request.itemPersonalMetadataId(),
                request.score());
    }

    public void updateEntityFromRequest(UserRatingRequest request, UserRating entity) {
        entity.setUserId(request.userId());
        entity.setItemPersonalMetadataId(request.itemPersonalMetadataId());
        entity.setScore(request.score());
    }
}
