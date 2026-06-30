package notkirill.workshopmanager.models.dto;

public record SteamWorkshopTagResponse(
        Long tagId,
        String tag,
        String displayName,
        Long consumerAppId
) {
}
