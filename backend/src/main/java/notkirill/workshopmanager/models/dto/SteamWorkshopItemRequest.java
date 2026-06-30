package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record SteamWorkshopItemRequest(
        @NotNull String publishedFileId,
        String creator,
        Long creatorAppId,
        Long consumerAppId,
        String fileSize,
        String previewUrl,
        String title,
        String description,
        OffsetDateTime timeCreated,
        OffsetDateTime timeUpdated,
        Short visibility,
        Integer subscriptions,
        Integer favorited,
        Integer lifetimeSubscriptions,
        Integer lifetimeFavorited
) {
}
