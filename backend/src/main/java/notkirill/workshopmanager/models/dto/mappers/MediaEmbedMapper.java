package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaEmbed;
import notkirill.workshopmanager.models.dto.MediaEmbedRequest;
import notkirill.workshopmanager.models.dto.MediaEmbedResponse;

public class MediaEmbedMapper {
    public MediaEmbedResponse fromEntity(MediaEmbed entity) {
        return new MediaEmbedResponse(entity.getMediaItemId(),
                entity.getExternalId(),
                entity.getProvider(),
                entity.getMetadata());
    }

    public MediaEmbed toEntity(MediaEmbedRequest request) {
        return new MediaEmbed(request.mediaItemId(),
                request.externalId(),
                request.provider(),
                request.metadata());
    }
}
