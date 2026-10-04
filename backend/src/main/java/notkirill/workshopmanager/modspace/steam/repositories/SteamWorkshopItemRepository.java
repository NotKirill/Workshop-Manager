package notkirill.workshopmanager.modspace.steam.repositories;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SteamWorkshopItemRepository extends JpaRepository<SteamWorkshopItem, Long> {
    Optional<SteamWorkshopItem> findByPublishedFileId(String publishedFileId);

    List<SteamWorkshopItem> findByPublishedFileIdIn(Collection<String> publishedFileIds);

    List<SteamWorkshopItem> findByCreator(String creator);

    List<SteamWorkshopItem> findByConsumerAppId(long consumerAppId);

    List<SteamWorkshopItem> findByVisibility(short visibility);

    List<SteamWorkshopItem> findByConsumerAppIdAndVisibility(long consumerAppId, short visibility);

    List<SteamWorkshopItem> findByConsumerAppIdAndCreator(long consumerAppId, String creator);

    List<SteamWorkshopItem> findByTimeCreatedBetween(OffsetDateTime timeCreatedStart, OffsetDateTime timeCreatedEnd);

    List<SteamWorkshopItem> findByTimeCreatedLessThan(OffsetDateTime timeCreated);

    List<SteamWorkshopItem> findByTimeCreatedLessThanEqual(OffsetDateTime timeCreated);

    List<SteamWorkshopItem> findByTimeCreatedGreaterThan(OffsetDateTime timeCreated);

    List<SteamWorkshopItem> findByTimeCreatedGreaterThanEqual(OffsetDateTime timeCreated);

    List<SteamWorkshopItem> findByTimeUpdatedBetween(OffsetDateTime timeUpdatedStart, OffsetDateTime timeUpdatedEnd);

    List<SteamWorkshopItem> findByTimeUpdatedLessThan(OffsetDateTime timeUpdated);

    List<SteamWorkshopItem> findByTimeUpdatedLessThanEqual(OffsetDateTime timeUpdated);

    List<SteamWorkshopItem> findByTimeUpdatedGreaterThan(OffsetDateTime timeUpdated);

    List<SteamWorkshopItem> findByTimeUpdatedGreaterThanEqual(OffsetDateTime timeUpdated);
}
