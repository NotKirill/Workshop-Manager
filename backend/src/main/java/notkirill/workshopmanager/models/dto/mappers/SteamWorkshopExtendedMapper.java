package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopExtended;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedResponse;

public class SteamWorkshopExtendedMapper {
    public SteamWorkshopExtendedResponse fromEntity(SteamWorkshopExtended entity) {
        return new SteamWorkshopExtendedResponse(entity.getInternalId(),
                entity.getPreviewFileSize(),
                entity.getShortDescription(),
                entity.getNumChildren(),
                entity.getPreviews(),
                entity.getChildren(),
                entity.getKvtags(),
                entity.isMaybeInappropriateSex(),
                entity.isMaybeInappropriateViolence(),
                entity.getContentDescriptorIds());

    }

    public SteamWorkshopExtended toEntity(SteamWorkshopExtendedRequest request) {
        return new SteamWorkshopExtended(request.internalId(),
                request.previewFileSize(),
                request.shortDescription(),
                request.numChildren(),
                request.previews(),
                request.children(),
                request.kvtags(),
                request.maybeInappropriateSex(),
                request.maybeInappropriateViolence(),
                request.contentDescriptorIds());
    }
}
