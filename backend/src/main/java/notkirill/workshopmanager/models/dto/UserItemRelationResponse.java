package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.RelationType;

public record UserItemRelationResponse(
        Long sourceItemPersonalMetadataId,
        Long targetItemPersonalMetadataId,
        RelationType relation
) {
}
