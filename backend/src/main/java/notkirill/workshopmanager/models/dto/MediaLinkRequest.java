package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.EntityType;
import tools.jackson.databind.JsonNode;

public record MediaLinkRequest(
        @NotNull Long mediaItemId,
        @NotNull EntityType entity,
        @NotNull Long entityId,
        @NotNull String purpose,
        Integer sortOrder,
        JsonNode metadata
) {
}
