package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record SteamWorkshopTagRequest(
        @NotNull String tag,
        String displayName,
        @NotNull Long consumerAppId
) {
}
