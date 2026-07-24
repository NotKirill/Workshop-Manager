package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.StoringStrategy;

public record ItemMetadataPersonalRequest(
        @NotNull Long userId,
        @NotNull Long internalMetadataId,
        String title,
        String description,
        String userCommentary,
        @NotNull StoringStrategy storingMode
) {
}
