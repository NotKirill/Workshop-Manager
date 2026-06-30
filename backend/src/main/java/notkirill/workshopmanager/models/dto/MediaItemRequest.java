package notkirill.workshopmanager.models.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.MediaType;

import java.time.OffsetDateTime;

public record MediaItemRequest(
        @NotNull MediaType type,
        @NotNull OffsetDateTime timeUploaded,
        @Nullable Long ownerUserId
) {
}
