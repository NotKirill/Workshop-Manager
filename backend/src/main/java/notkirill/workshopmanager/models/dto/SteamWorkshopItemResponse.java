package notkirill.workshopmanager.models.dto;

import java.time.OffsetDateTime;

public record SteamWorkshopItemResponse(
        Long IntegerernalId,
        String publishedFileId,
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
