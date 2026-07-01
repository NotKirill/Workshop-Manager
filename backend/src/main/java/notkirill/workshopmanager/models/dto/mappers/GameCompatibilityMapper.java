package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.GameCompatibility;
import notkirill.workshopmanager.models.dto.GameCompatibilityRequest;
import notkirill.workshopmanager.models.dto.GameCompatibilityResponse;

public class GameCompatibilityMapper {
    public GameCompatibilityResponse entityToResponse(GameCompatibility entity) {
        return new GameCompatibilityResponse(entity.getSourceGameId(),
                entity.getTagetGameId());
    }

    public GameCompatibility requestToEntity(GameCompatibilityRequest request) {
        return new GameCompatibility(request.sourceGameId(),
                request.tagetGameId());
    }

    public void updateEntityFromRequest(GameCompatibilityRequest request, GameCompatibility entity) {
        entity.setSourceGameId(request.sourceGameId());
        entity.setTagetGameId(request.tagetGameId());
    }
}