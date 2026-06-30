package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.RelationType;

public record UserItemRelationRequest(
        @NotNull Long sourceItemPersonalMetadataId,
        @NotNull Long targetItemPersonalMetadataId,
        @NotNull RelationType relation
) {
}
