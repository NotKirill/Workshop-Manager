package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.AuthorMetadata;
import notkirill.workshopmanager.models.dto.AuthorMetadataRequest;
import notkirill.workshopmanager.models.dto.AuthorMetadataResponse;

public class AuthorMetadataMapper {
    public AuthorMetadataResponse entityToResponse(AuthorMetadata entity) {
        return new AuthorMetadataResponse(entity.getId(),
                entity.getSourceModspace(),
                entity.getOriginalId(),
                entity.getName());
    }

    public AuthorMetadata requestToEntity(AuthorMetadataRequest request) {
        return new AuthorMetadata(request.sourceModspace(),
                request.originalId(),
                request.name());
    }

    public void updateEntityFromRequest(AuthorMetadataRequest request, AuthorMetadata entity) {
        entity.setSourceModspace(request.sourceModspace());
        entity.setOriginalId(request.originalId());
        entity.setName(request.name());
    }
}
