package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopExtended;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedResponse;

public class SteamWorkshopExtendedMapper {
    public SteamWorkshopExtendedResponse entityToResponse(SteamWorkshopExtended entity) {
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

    public SteamWorkshopExtended requestToEntity(SteamWorkshopExtendedRequest request) {
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

    public void updateEntityFromRequest(SteamWorkshopExtendedRequest request, SteamWorkshopExtended entity) {
        entity.setInternalId(request.internalId());
        entity.setPreviewFileSize(request.previewFileSize());
        entity.setShortDescription(request.shortDescription());
        entity.setNumChildren(request.numChildren());
        entity.setPreviews(request.previews());
        entity.setChildren(request.children());
        entity.setKvtags(request.kvtags());
        entity.setMaybeInappropriateSex(request.maybeInappropriateSex());
        entity.setMaybeInappropriateViolence(request.maybeInappropriateViolence());
        entity.setContentDescriptorIds(request.contentDescriptorIds());
    }
}
