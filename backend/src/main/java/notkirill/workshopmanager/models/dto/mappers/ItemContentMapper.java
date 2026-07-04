package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemContent;
import notkirill.workshopmanager.models.dto.ItemContentRequest;
import notkirill.workshopmanager.models.dto.ItemContentResponse;
import org.springframework.stereotype.Component;

@Component
public class ItemContentMapper {
    public ItemContentResponse entityToResponse(ItemContent entity) {
        return new ItemContentResponse(entity.getId(),
                entity.getInternalMetadataId(),
                entity.getReferenceCount(),
                entity.getEdition(),
                entity.getAttributes(),
                entity.getMaxRevisions());
    }

    public ItemContent requestToEntity(ItemContentRequest request) {
        return new ItemContent(request.internalMetadataId(),
                request.referenceCount(),
                request.edition(),
                request.attributes(),
                request.maxRevisions());
    }

    public void updateEntityFromRequest(ItemContentRequest request, ItemContent entity) {
        entity.setInternalMetadataId(request.internalMetadataId());
        entity.setReferenceCount(request.referenceCount());
        entity.setEdition(request.edition());
        entity.setAttributes(request.attributes());
        entity.setMaxRevisions(request.maxRevisions());
    }
}
