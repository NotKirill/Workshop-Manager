package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaLink;
import notkirill.workshopmanager.models.dto.MediaLinkRequest;
import notkirill.workshopmanager.models.dto.MediaLinkResponse;

public class MediaLinkMapper {
    public MediaLinkResponse fromEntity(MediaLink entity) {
        return new MediaLinkResponse(entity.getId(),
                entity.getMediaItemId(),
                entity.getEntity(),
                entity.getEntityId(),
                entity.getPurpose(),
                entity.getSortOrder(),
                entity.getMetadata());


    }

    public MediaLink toEntity(MediaLinkRequest request) {
        return new MediaLink(request.mediaItemId(),
                request.entity(),
                request.entityId(),
                request.purpose(),
                request.sortOrder(),
                request.metadata());
    }
}
