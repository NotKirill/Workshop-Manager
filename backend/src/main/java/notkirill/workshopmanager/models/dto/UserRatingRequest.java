package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record UserRatingRequest(
        @NotNull Long userId,
        @NotNull Long itemPersonalMetadataId,
        Short score
) {
}
