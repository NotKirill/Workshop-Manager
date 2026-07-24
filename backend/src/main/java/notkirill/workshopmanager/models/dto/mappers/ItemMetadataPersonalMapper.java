package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemMetadataPersonal;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalResponse;
import org.springframework.stereotype.Component;

@Component
public class ItemMetadataPersonalMapper {
    public ItemMetadataPersonalResponse entityToResponse(ItemMetadataPersonal entity) {
        return new ItemMetadataPersonalResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getInternalMetadataId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getUserCommentary(),
                entity.getStoringMode()
        );
    }

    public ItemMetadataPersonal requestToEntity(ItemMetadataPersonalRequest request) {
        return new ItemMetadataPersonal(
                request.userId(),
                request.internalMetadataId(),
                request.title(),
                request.description(),
                request.userCommentary(),
                request.storingMode()
        );
    }

    public void updateEntityFromRequest(ItemMetadataPersonalRequest request, ItemMetadataPersonal entity) {
        entity.setUserId(request.userId());
        entity.setInternalMetadataId(request.internalMetadataId());
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setUserCommentary(request.userCommentary());
        entity.setStoringMode(request.storingMode());
    }
}
