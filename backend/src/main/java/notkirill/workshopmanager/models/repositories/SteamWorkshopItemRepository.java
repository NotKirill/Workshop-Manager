package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.SteamWorkshopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface SteamWorkshopItemRepository extends JpaRepository<SteamWorkshopItem, Long> {
    Optional<SteamWorkshopItem> findByPublishedFileId(String publishedFileId);

    Optional<SteamWorkshopItem> findByCreator(String creator);

    Optional<SteamWorkshopItem> findByConsumerAppId(long consumerAppId);

    Optional<SteamWorkshopItem> findByVisibility(short visibility);

    Optional<SteamWorkshopItem> findByConsumerAppIdAndVisibility(long consumerAppId, short visibility);

    Optional<SteamWorkshopItem> findByConsumerAppIdAndCreator(long consumerAppId, String creator);

    Optional<SteamWorkshopItem> findByTimeCreatedBetween(OffsetDateTime timeCreatedStart, OffsetDateTime timeCreatedEnd);

    Optional<SteamWorkshopItem> findByTimeCreatedLessThan(OffsetDateTime timeCreated);

    Optional<SteamWorkshopItem> findByTimeCreatedLessThanEqual(OffsetDateTime timeCreated);

    Optional<SteamWorkshopItem> findByTimeCreatedGreaterThan(OffsetDateTime timeCreated);

    Optional<SteamWorkshopItem> findByTimeCreatedGreaterThanEqual(OffsetDateTime timeCreated);

    Optional<SteamWorkshopItem> findByTimeUpdatedBetween(OffsetDateTime timeUpdatedStart, OffsetDateTime timeUpdatedEnd);

    Optional<SteamWorkshopItem> findByTimeUpdatedLessThan(OffsetDateTime timeUpdated);

    Optional<SteamWorkshopItem> findByTimeUpdatedLessThanEqual(OffsetDateTime timeUpdated);

    Optional<SteamWorkshopItem> findByTimeUpdatedGreaterThan(OffsetDateTime timeUpdated);

    Optional<SteamWorkshopItem> findByTimeUpdatedGreaterThanEqual(OffsetDateTime timeUpdated);


}