package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagResponse;
import org.springframework.stereotype.Component;

@Component
public class SteamWorkshopItemTagMapper {
    public SteamWorkshopItemTagResponse entityToResponse(SteamWorkshopItemTag entity) {
        return new SteamWorkshopItemTagResponse(entity.getInternalId(),
                entity.getTagId());
    }

    public SteamWorkshopItemTag requestToEntity(SteamWorkshopItemTagRequest request) {
        return new SteamWorkshopItemTag(request.internalId(),
                request.tagId());
    }

    public void updateEntityFromRequest(SteamWorkshopItemTagRequest request, SteamWorkshopItemTag entity) {
        entity.setInternalId(request.internalId());
        entity.setTagId(request.tagId());
    }
}
