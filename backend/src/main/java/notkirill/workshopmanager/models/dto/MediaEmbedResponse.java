package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.MediaProvider;
import tools.jackson.databind.JsonNode;

public record MediaEmbedResponse(
        Long mediaItemId,
        String externalId,
        MediaProvider provider,
        JsonNode metadata
) {
}
