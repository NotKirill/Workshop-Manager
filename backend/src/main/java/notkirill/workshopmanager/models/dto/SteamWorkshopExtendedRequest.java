package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

public record SteamWorkshopExtendedRequest(
        @NotNull Long internalId,
        String previewFileSize,
        String shortDescription,
        Short numChildren,
        JsonNode previews,
        JsonNode children,
        JsonNode kvtags,
        Boolean maybeInappropriateSex,
        Boolean maybeInappropriateViolence,
        short[] contentDescriptorIds
) {
}
