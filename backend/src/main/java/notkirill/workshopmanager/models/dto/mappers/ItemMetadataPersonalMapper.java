package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemMetadataPersonal;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalResponse;
import org.springframework.stereotype.Component;

@Component
public class ItemMetadataPersonalMapper {
    public ItemMetadataPersonalResponse entityToResponse(ItemMetadataPersonal entity) {
        return new ItemMetadataPersonalResponse(entity.getId(),
                entity.getUserId(),
                entity.getInternalMetadataId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getUserCommentary(),
                entity.isMetadataOnly(),
                entity.isContentImport());
    }

    public ItemMetadataPersonal requestToEntity(ItemMetadataPersonalRequest request) {
        return new ItemMetadataPersonal(request.userId(),
                request.internalMetadataId(),
                request.title(),
                request.description(),
                request.userCommentary(),
                request.metadataOnly(),
                request.contentImport());
    }

    public void updateEntityFromRequest(ItemMetadataPersonalRequest request, ItemMetadataPersonal entity) {
        entity.setUserId(request.userId());
        entity.setInternalMetadataId(request.internalMetadataId());
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setUserCommentary(request.userCommentary());
        entity.setMetadataOnly(request.metadataOnly());
        entity.setContentImport(request.contentImport());
    }
}
