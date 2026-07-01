package notkirill.workshopmanager.models.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ItemContentRequest(
        @NotNull Long internalMetadataId,
        Integer referenceCount,
        @NotNull String edition,
        @Nullable String[] attributes,
        Short maxRevisions
) {
}
