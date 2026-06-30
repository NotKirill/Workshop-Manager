package notkirill.workshopmanager.models.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record MediaLinkOverrideRequest(
        @NotNull Long linkId,
        @NotNull Long userId,
        @Nullable Long mediaItemId,
        Integer sortOrder
) {
}
