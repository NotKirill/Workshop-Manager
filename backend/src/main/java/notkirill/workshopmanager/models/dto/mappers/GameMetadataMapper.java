package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.GameMetadata;
import notkirill.workshopmanager.models.dto.GameMetadataRequest;
import notkirill.workshopmanager.models.dto.GameMetadataResponse;
import org.springframework.stereotype.Component;

@Component
public class GameMetadataMapper {
    public GameMetadataResponse entityToResponse(GameMetadata entity) {
        return new GameMetadataResponse(entity.getId(),
                entity.getName());
    }

    public GameMetadata requestToEntity(GameMetadataRequest request) {
        return new GameMetadata(request.name());
    }

    public void updateEntityFromRequest(GameMetadataRequest request, GameMetadata entity) {
        entity.setName(request.name());
    }
}