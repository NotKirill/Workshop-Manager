package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserItemRelation;
import notkirill.workshopmanager.models.dto.UserItemRelationRequest;
import notkirill.workshopmanager.models.dto.UserItemRelationResponse;

public class UserItemRelationMapper {
    public UserItemRelationResponse fromEntity(UserItemRelation entity) {
        return new UserItemRelationResponse(entity.getSourceItemPersonalMetadataId(),
                entity.getTargetItemPersonalMetadataId(),
                entity.getRelation());


    }

    public UserItemRelation toEntity(UserItemRelationRequest request) {
        return new UserItemRelation(request.sourceItemPersonalMetadataId(),
                request.targetItemPersonalMetadataId(),
                request.relation());
    }
}
