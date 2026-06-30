package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaLinkOverride;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideRequest;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideResponse;

public class MediaLinkOverrideMapper {
    public MediaLinkOverrideResponse fromEntity(MediaLinkOverride entity) {
        return new MediaLinkOverrideResponse(entity.getLinkId(),
                entity.getUserId(),
                entity.getMediaItemId(),
                entity.getSortOrder());

    }

    @SuppressWarnings("DataFlowIssue")
    public MediaLinkOverride toEntity(MediaLinkOverrideRequest request) {
        return new MediaLinkOverride(request.linkId(),
                request.userId(),
                request.mediaItemId(),
                request.sortOrder());
    }
}
