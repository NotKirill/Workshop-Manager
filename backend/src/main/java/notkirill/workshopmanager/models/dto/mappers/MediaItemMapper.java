package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaItem;
import notkirill.workshopmanager.models.dto.MediaItemRequest;
import notkirill.workshopmanager.models.dto.MediaItemResponse;

public class MediaItemMapper {
    public MediaItemResponse fromEntity(MediaItem entity) {
        return new MediaItemResponse(entity.getId(),
                entity.getType(),
                entity.getTimeUploaded(),
                entity.getOwnerUserId());

    }

    public MediaItem toEntity(MediaItemRequest request) {
        return new MediaItem(request.type(),
                request.timeUploaded(),
                request.ownerUserId());
    }
}
