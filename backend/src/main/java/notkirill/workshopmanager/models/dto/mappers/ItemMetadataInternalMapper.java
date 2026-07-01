package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemMetadataInternal;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalResponse;

public class ItemMetadataInternalMapper {
    public ItemMetadataInternalResponse entityToResponse(ItemMetadataInternal entity) {
        return new ItemMetadataInternalResponse(entity.getId(),
                entity.getSourceModspace(),
                entity.getGameId(),
                entity.getOriginalId(),
                entity.getTimeRegistered(),
                entity.getTimeUpdated());
    }

    public ItemMetadataInternal requestToEntity(ItemMetadataInternalRequest request) {
        return new ItemMetadataInternal(request.sourceModspace(),
                request.gameId(),
                request.originalId(),
                request.timeRegistered(),
                request.timeUpdated());
    }

    public void updateEntityFromRequest(ItemMetadataInternalRequest request, ItemMetadataInternal entity) {
        entity.setSourceModspace(request.sourceModspace());
        entity.setGameId(request.gameId());
        entity.setOriginalId(request.originalId());
        entity.setTimeRegistered(request.timeRegistered());
        entity.setTimeUpdated(request.timeUpdated());
    }
}
