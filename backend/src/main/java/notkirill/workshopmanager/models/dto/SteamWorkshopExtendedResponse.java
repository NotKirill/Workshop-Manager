package notkirill.workshopmanager.models.dto;

import tools.jackson.databind.JsonNode;

public record SteamWorkshopExtendedResponse(
        Long internalId,
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
