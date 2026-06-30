package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record SteamWorkshopItemTagRequest(
        @NotNull Long internalId,
        @NotNull Long tagId
) {
}
