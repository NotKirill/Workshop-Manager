package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.GameToModspaceId;
import notkirill.workshopmanager.models.dto.GameToModspaceIdRequest;
import notkirill.workshopmanager.models.dto.GameToModspaceIdResponse;


public class GameToModspaceIdMapper {
    public GameToModspaceIdResponse entityToResponse(GameToModspaceId entity) {
        return new GameToModspaceIdResponse(entity.getGameId(),
                entity.getSourceModspace(),
                entity.getExternalId());
    }

    public GameToModspaceId requestToEntity(GameToModspaceIdRequest request) {
        return new GameToModspaceId(request.gameId(),
                request.sourceModspace(),
                request.externalId());
    }

    public void updateEntityFromRequest(GameToModspaceIdRequest request, GameToModspaceId entity) {
        entity.setGameId(request.gameId());
        entity.setSourceModspace(request.sourceModspace());
        entity.setExternalId(request.externalId());
    }
}
