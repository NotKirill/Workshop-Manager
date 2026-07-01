package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopTag;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagResponse;

public class SteamWorkshopTagMapper {
    public SteamWorkshopTagResponse entityToResponse(SteamWorkshopTag entity) {
        return new SteamWorkshopTagResponse(entity.getTagId(),
                entity.getTag(),
                entity.getDisplayName(),
                entity.getConsumerAppId());
    }

    public SteamWorkshopTag requestToEntity(SteamWorkshopTagRequest request) {
        return new SteamWorkshopTag(request.tag(),
                request.displayName(),
                request.consumerAppId());
    }

    public void updateEntityFromRequest(SteamWorkshopTagRequest request, SteamWorkshopTag entity) {
        entity.setTag(request.tag());
        entity.setDisplayName(request.displayName());
        entity.setConsumerAppId(request.consumerAppId());
    }
}
