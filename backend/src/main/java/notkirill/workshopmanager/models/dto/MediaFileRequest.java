package notkirill.workshopmanager.models.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

public record MediaFileRequest(
        @NotNull Long mediaItemId,
        @Nullable String sourceId,
        @NotNull Integer fileSize,
        @NotNull String fileName,
        @NotNull String s3Key,
        JsonNode metadata
) {
}
