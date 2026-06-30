package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record ItemContentRequest(
        @NotNull Long internalMetadataId,
        Integer referenceCount,
        @NotNull String edition,
        String[] attributes,
        Short maxRevisions
) {
}
