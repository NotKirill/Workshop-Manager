package notkirill.workshopmanager.models.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.MediaType;

public record MediaItemRequest(
        @NotNull MediaType type,
        @Nullable Long ownerUserId
) {
}
