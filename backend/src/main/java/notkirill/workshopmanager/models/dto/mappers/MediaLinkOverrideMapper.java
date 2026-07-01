package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaLinkOverride;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideRequest;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideResponse;

public class MediaLinkOverrideMapper {
    public MediaLinkOverrideResponse entityToResponse(MediaLinkOverride entity) {
        return new MediaLinkOverrideResponse(entity.getLinkId(),
                entity.getUserId(),
                entity.getMediaItemId(),
                entity.getSortOrder());
    }

    @SuppressWarnings("DataFlowIssue")
    public MediaLinkOverride requestToEntity(MediaLinkOverrideRequest request) {
        return new MediaLinkOverride(request.linkId(),
                request.userId(),
                request.mediaItemId(),
                request.sortOrder());
    }

    @SuppressWarnings("DataFlowIssue")
    public void updateEntityFromRequest(MediaLinkOverrideRequest request, MediaLinkOverride entity) {
        entity.setLinkId(request.linkId());
        entity.setUserId(request.userId());
        entity.setMediaItemId(request.mediaItemId());
        entity.setSortOrder(request.sortOrder());
    }
}
