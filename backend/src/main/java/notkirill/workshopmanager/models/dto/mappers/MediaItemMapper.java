package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaItem;
import notkirill.workshopmanager.models.dto.MediaItemRequest;
import notkirill.workshopmanager.models.dto.MediaItemResponse;

public class MediaItemMapper {
    public MediaItemResponse entityToResponse(MediaItem entity) {
        return new MediaItemResponse(entity.getId(),
                entity.getType(),
                entity.getTimeUploaded(),
                entity.getOwnerUserId());
    }

    public MediaItem requestToEntity(MediaItemRequest request) {
        return new MediaItem(request.type(),
                request.timeUploaded(),
                request.ownerUserId());
    }

    public void updateEntityFromRequest(MediaItemRequest request, MediaItem entity) {
        entity.setType(request.type());
        entity.setTimeUploaded(request.timeUploaded());
        entity.setOwnerUserId(request.ownerUserId());
    }
}
