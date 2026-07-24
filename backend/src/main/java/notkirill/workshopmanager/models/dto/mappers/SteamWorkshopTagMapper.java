package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopTag;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagResponse;
import org.springframework.stereotype.Component;

@Component
public class SteamWorkshopTagMapper {
    public SteamWorkshopTagResponse entityToResponse(SteamWorkshopTag entity) {
        return new SteamWorkshopTagResponse(
                entity.getTagId(),
                entity.getTagKey(),
                entity.getDisplayName(),
                entity.getConsumerAppId()
        );
    }

    public SteamWorkshopTag requestToEntity(SteamWorkshopTagRequest request) {
        return new SteamWorkshopTag(
                request.tagKey(),
                request.displayName(),
                request.consumerAppId()
        );
    }

    public void updateEntityFromRequest(SteamWorkshopTagRequest request, SteamWorkshopTag entity) {
        entity.setTagKey(request.tagKey());
        entity.setDisplayName(request.displayName());
        entity.setConsumerAppId(request.consumerAppId());
    }
}
