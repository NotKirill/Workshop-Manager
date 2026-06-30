package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopItem;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemResponse;

public class SteamWorkshopItemMapper {
    public SteamWorkshopItemResponse fromEntity(SteamWorkshopItem entity) {
        return new SteamWorkshopItemResponse(entity.getInternalId(),
                entity.getPublishedFileId(),
                entity.getCreator(),
                entity.getCreatorAppId(),
                entity.getConsumerAppId(),
                entity.getFileSize(),
                entity.getPreviewUrl(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getTimeCreated(),
                entity.getTimeUpdated(),
                entity.getVisibility(),
                entity.getSubscriptions(),
                entity.getFavorited(),
                entity.getLifetimeSubscriptions(),
                entity.getLifetimeFavorited());

    }

    public SteamWorkshopItem toEntity(SteamWorkshopItemRequest request) {
        return new SteamWorkshopItem(request.publishedFileId(),
                request.creator(),
                request.creatorAppId(),
                request.consumerAppId(),
                request.fileSize(),
                request.previewUrl(),
                request.title(),
                request.description(),
                request.timeCreated(),
                request.timeUpdated(),
                request.visibility(),
                request.subscriptions(),
                request.favorited(),
                request.lifetimeSubscriptions(),
                request.lifetimeFavorited());
    }
}
