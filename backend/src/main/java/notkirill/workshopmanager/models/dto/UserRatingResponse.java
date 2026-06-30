package notkirill.workshopmanager.models.dto;

public record UserRatingResponse(
        Long userId,
        Long itemPersonalMetadataId,
        Short score
) {
}
