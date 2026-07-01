package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserItemRelation;
import notkirill.workshopmanager.models.dto.UserItemRelationRequest;
import notkirill.workshopmanager.models.dto.UserItemRelationResponse;

public class UserItemRelationMapper {
    public UserItemRelationResponse entityToResponse(UserItemRelation entity) {
        return new UserItemRelationResponse(entity.getSourceItemPersonalMetadataId(),
                entity.getTargetItemPersonalMetadataId(),
                entity.getRelation());
    }

    public UserItemRelation requestToEntity(UserItemRelationRequest request) {
        return new UserItemRelation(request.sourceItemPersonalMetadataId(),
                request.targetItemPersonalMetadataId(),
                request.relation());
    }

    public void updateEntityFromRequest(UserItemRelationRequest request, UserItemRelation entity) {
        entity.setSourceItemPersonalMetadataId(request.sourceItemPersonalMetadataId());
        entity.setTargetItemPersonalMetadataId(request.targetItemPersonalMetadataId());
        entity.setRelation(request.relation());
    }
}
