package notkirill.workshopmanager.models.dto;

public record SteamWorkshopTagResponse(
        Long tagId,
        String tagKey,
        String displayName,
        Long consumerAppId
) {
}
