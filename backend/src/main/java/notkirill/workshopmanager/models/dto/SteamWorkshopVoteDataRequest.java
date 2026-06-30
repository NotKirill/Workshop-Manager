package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record SteamWorkshopVoteDataRequest(
        @NotNull Long internalId,
        Float score,
        Integer votesUp,
        Integer votesDown
) {
}
