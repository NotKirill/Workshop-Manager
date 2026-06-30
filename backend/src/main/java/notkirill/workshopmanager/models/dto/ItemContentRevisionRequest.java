package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ItemContentRevisionRequest(
        @NotNull Long itemContentId,
        @NotNull Long revision,
        @NotNull String s3Key,
        @NotNull String fileName,
        @NotNull Integer fileSize,
        @NotNull OffsetDateTime timeCreated
) {
}
