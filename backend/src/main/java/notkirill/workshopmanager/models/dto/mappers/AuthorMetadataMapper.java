package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.AuthorMetadata;
import notkirill.workshopmanager.models.dto.AuthorMetadataRequest;
import notkirill.workshopmanager.models.dto.AuthorMetadataResponse;

public class AuthorMetadataMapper {
    public AuthorMetadataResponse fromEntity(AuthorMetadata entity) {
        return new AuthorMetadataResponse(entity.getId(),
                entity.getSourceModspace(),
                entity.getOriginalId(),
                entity.getName());
    }

    public AuthorMetadata toEntity(AuthorMetadataRequest request) {
        return new AuthorMetadata(request.sourceModspace(),
                request.originalId(),
                request.name());
    }
}
