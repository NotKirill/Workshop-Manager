package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.GameCompatibility;
import notkirill.workshopmanager.models.dto.GameCompatibilityRequest;
import notkirill.workshopmanager.models.dto.GameCompatibilityResponse;

public class GameCompatibilityMapper {
    public GameCompatibilityResponse fromEntity(GameCompatibility entity) {
        return new GameCompatibilityResponse(entity.getSourceGameId(),
                entity.getTagetGameId());
    }

    public GameCompatibility toEntity(GameCompatibilityRequest request) {
        return new GameCompatibility(request.sourceGameId(),
                request.tagetGameId());
    }
}