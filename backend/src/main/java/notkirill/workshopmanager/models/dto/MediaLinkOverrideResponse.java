package notkirill.workshopmanager.models.dto;

public record MediaLinkOverrideResponse(
        Long linkId,
        Long userId,
        Long mediaItemId,
        Integer sortOrder
) {
}
