package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.GameMetadata;
import notkirill.workshopmanager.models.dto.GameMetadataRequest;
import notkirill.workshopmanager.models.dto.GameMetadataResponse;

public class GameMetadataMapper {
    public GameMetadataResponse fromEntity(GameMetadata entity) {
        return new GameMetadataResponse(entity.getId(),
                entity.getName());
    }

    public GameMetadata toEntity(GameMetadataRequest request) {
        return new GameMetadata(request.name());
    }
}