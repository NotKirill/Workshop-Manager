package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaLink;
import notkirill.workshopmanager.models.dto.MediaLinkRequest;
import notkirill.workshopmanager.models.dto.MediaLinkResponse;
import org.springframework.stereotype.Component;

@Component
public class MediaLinkMapper {
    public MediaLinkResponse entityToResponse(MediaLink entity) {
        return new MediaLinkResponse(entity.getId(),
                entity.getMediaItemId(),
                entity.getEntity(),
                entity.getEntityId(),
                entity.getPurpose(),
                entity.getSortOrder(),
                entity.getMetadata());
    }

    public MediaLink requestToEntity(MediaLinkRequest request) {
        return new MediaLink(request.mediaItemId(),
                request.entity(),
                request.entityId(),
                request.purpose(),
                request.sortOrder(),
                request.metadata());
    }

    public void updateEntityFromRequest(MediaLinkRequest request, MediaLink entity) {
        entity.setMediaItemId(request.mediaItemId());
        entity.setEntity(request.entity());
        entity.setEntityId(request.entityId());
        entity.setPurpose(request.purpose());
        entity.setSortOrder(request.sortOrder());
        entity.setMetadata(request.metadata());
    }
}
