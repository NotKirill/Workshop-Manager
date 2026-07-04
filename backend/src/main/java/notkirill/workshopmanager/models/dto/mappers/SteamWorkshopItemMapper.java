package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopItem;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemResponse;
import org.springframework.stereotype.Component;

@Component
public class SteamWorkshopItemMapper {
    public SteamWorkshopItemResponse entityToResponse(SteamWorkshopItem entity) {
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

    public SteamWorkshopItem requestToEntity(SteamWorkshopItemRequest request) {
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

    public void updateEntityFromRequest(SteamWorkshopItemRequest request, SteamWorkshopItem entity) {
        entity.setPublishedFileId(request.publishedFileId());
        entity.setCreator(request.creator());
        entity.setCreatorAppId(request.creatorAppId());
        entity.setConsumerAppId(request.consumerAppId());
        entity.setFileSize(request.fileSize());
        entity.setPreviewUrl(request.previewUrl());
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setTimeCreated(request.timeCreated());
        entity.setTimeUpdated(request.timeUpdated());
        entity.setVisibility(request.visibility());
        entity.setSubscriptions(request.subscriptions());
        entity.setFavorited(request.favorited());
        entity.setLifetimeSubscriptions(request.lifetimeSubscriptions());
        entity.setLifetimeFavorited(request.lifetimeFavorited());
    }
}
