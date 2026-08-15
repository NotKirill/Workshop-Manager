package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record ItemContentRevisionRequest(
        @NotNull Long itemContentId,
        @NotNull Long revision,
        @NotNull String s3Key,
        @NotNull String fileName,
        @NotNull Integer fileSize
) {
}
