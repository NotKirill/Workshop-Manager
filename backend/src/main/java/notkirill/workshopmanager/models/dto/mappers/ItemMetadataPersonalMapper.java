package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemMetadataPersonal;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalResponse;

public class ItemMetadataPersonalMapper {
    public ItemMetadataPersonalResponse fromEntity(ItemMetadataPersonal entity) {
        return new ItemMetadataPersonalResponse(entity.getId(),
                entity.getUserId(),
                entity.getInternalMetadataId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getUserCommentary(),
                entity.isMetadataOnly(),
                entity.isContentImport());
    }

    public ItemMetadataPersonal toEntity(ItemMetadataPersonalRequest request) {
        return new ItemMetadataPersonal(request.userId(),
                request.internalMetadataId(),
                request.title(),
                request.description(),
                request.userCommentary(),
                request.metadataOnly(),
                request.contentImport());
    }
}
