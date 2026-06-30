package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.GameToModspaceId;
import notkirill.workshopmanager.models.dto.GameToModspaceIdRequest;
import notkirill.workshopmanager.models.dto.GameToModspaceIdResponse;


public class GameToModspaceIdMapper {
    public GameToModspaceIdResponse fromEntity(GameToModspaceId entity) {
        return new GameToModspaceIdResponse(entity.getGameId(),
                entity.getSourceModspace(),
                entity.getExternalId());
    }

    public GameToModspaceId toEntity(GameToModspaceIdRequest request) {
        return new GameToModspaceId(request.gameId(),
                request.sourceModspace(),
                request.externalId());
    }
}
