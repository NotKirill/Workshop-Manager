package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserRating;
import notkirill.workshopmanager.models.dto.UserRatingRequest;
import notkirill.workshopmanager.models.dto.UserRatingResponse;

public class UserRatingMapper {
    public UserRatingResponse fromEntity(UserRating entity) {
        return new UserRatingResponse(entity.getUserId(),
                entity.getItemPersonalMetadataId(),
                entity.getScore());


    }

    public UserRating toEntity(UserRatingRequest request) {
        return new UserRating(request.userId(),
                request.itemPersonalMetadataId(),
                request.score());
    }
}
