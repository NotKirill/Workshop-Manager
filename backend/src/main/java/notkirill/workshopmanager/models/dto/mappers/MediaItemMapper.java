package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaItem;
import notkirill.workshopmanager.models.dto.MediaItemRequest;
import notkirill.workshopmanager.models.dto.MediaItemResponse;
import org.springframework.stereotype.Component;

@Component
public class MediaItemMapper {
    public MediaItemResponse entityToResponse(MediaItem entity) {
        return new MediaItemResponse(
                entity.getId(),
                entity.getType(),
                entity.getTimeUploaded(),
                entity.getOwnerUserId()
        );
    }

    public MediaItem requestToEntity(MediaItemRequest request) {
        return new MediaItem(
                request.type(),
                request.ownerUserId()
        );
    }

    public void updateEntityFromRequest(MediaItemRequest request, MediaItem entity) {
        entity.setType(request.type());
        entity.setOwnerUserId(request.ownerUserId());
    }
}
