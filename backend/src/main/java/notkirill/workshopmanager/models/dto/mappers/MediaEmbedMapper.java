package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaEmbed;
import notkirill.workshopmanager.models.dto.MediaEmbedRequest;
import notkirill.workshopmanager.models.dto.MediaEmbedResponse;
import org.springframework.stereotype.Component;

@Component
public class MediaEmbedMapper {
    public MediaEmbedResponse entityToResponse(MediaEmbed entity) {
        return new MediaEmbedResponse(entity.getMediaItemId(),
                entity.getExternalId(),
                entity.getProvider(),
                entity.getMetadata());
    }

    public MediaEmbed requestToEntity(MediaEmbedRequest request) {
        return new MediaEmbed(request.mediaItemId(),
                request.externalId(),
                request.provider(),
                request.metadata());
    }

    public void updateEntityFromRequest(MediaEmbedRequest request, MediaEmbed entity) {
        entity.setMediaItemId(request.mediaItemId());
        entity.setExternalId(request.externalId());
        entity.setProvider(request.provider());
        entity.setMetadata(request.metadata());
    }
}
