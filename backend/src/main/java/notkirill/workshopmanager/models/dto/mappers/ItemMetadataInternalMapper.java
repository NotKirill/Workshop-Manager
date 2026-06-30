package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemMetadataInternal;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalResponse;

public class ItemMetadataInternalMapper {
    public ItemMetadataInternalResponse fromEntity(ItemMetadataInternal entity) {
        return new ItemMetadataInternalResponse(entity.getId(),
                entity.getSourceModspace(),
                entity.getGameId(),
                entity.getOriginalId(),
                entity.getTimeRegistered(),
                entity.getTimeUpdated());
    }

    public ItemMetadataInternal toEntity(ItemMetadataInternalRequest request) {
        return new ItemMetadataInternal(request.sourceModspace(),
                request.gameId(),
                request.originalId(),
                request.timeRegistered(),
                request.timeUpdated());
    }
}
