package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.MediaProvider;
import tools.jackson.databind.JsonNode;

public record MediaEmbedRequest(
        @NotNull Long mediaItemId,
        @NotNull String externalId,
        @NotNull MediaProvider provider,
        JsonNode metadata
) {
}
