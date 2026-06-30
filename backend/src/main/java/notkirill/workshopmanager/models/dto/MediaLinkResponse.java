package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.EntityType;
import tools.jackson.databind.JsonNode;

public record MediaLinkResponse(
        Long id,
        Long mediaItemId,
        EntityType entity,
        Long entityId,
        String purpose,
        Integer sortOrder,
        JsonNode metadata
) {
}
