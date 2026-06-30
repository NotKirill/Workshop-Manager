package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemContent;
import notkirill.workshopmanager.models.dto.ItemContentRequest;
import notkirill.workshopmanager.models.dto.ItemContentResponse;

public class ItemContentMapper {
    public ItemContentResponse fromEntity(ItemContent entity) {
        return new ItemContentResponse(entity.getId(),
                entity.getInternalMetadataId(),
                entity.getReferenceCount(),
                entity.getEdition(),
                entity.getAttributes(),
                entity.getMaxRevisions());
    }

    public ItemContent toEntity(ItemContentRequest request) {
        return new ItemContent(request.internalMetadataId(),
                request.referenceCount(),
                request.edition(),
                request.attributes(),
                request.maxRevisions());
    }
}
