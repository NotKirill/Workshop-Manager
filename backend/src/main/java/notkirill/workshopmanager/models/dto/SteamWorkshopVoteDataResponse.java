package notkirill.workshopmanager.models.dto;

public record SteamWorkshopVoteDataResponse(
        Long internalId,
        Float score,
        Integer votesUp,
        Integer votesDown
) {
}
