package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagResponse;

public class SteamWorkshopItemTagMapper {
    public SteamWorkshopItemTagResponse fromEntity(SteamWorkshopItemTag entity) {
        return new SteamWorkshopItemTagResponse(entity.getInternalId(),
                entity.getTagId());

    }

    public SteamWorkshopItemTag toEntity(SteamWorkshopItemTagRequest request) {
        return new SteamWorkshopItemTag(request.internalId(),
                request.tagId());
    }
}
