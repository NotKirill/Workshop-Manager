package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record ItemMetadataPersonalRequest(
        @NotNull Long userId,
        @NotNull Long internalMetadataId,
        String title,
        String description,
        String userCommentary,
        @NotNull Boolean metadataOnly,
        @NotNull Boolean contentImport
) {
}
