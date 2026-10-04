package notkirill.workshopmanager.modspace.steam.repositories;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItemTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SteamWorkshopItemTagRepository extends JpaRepository<SteamWorkshopItemTag, SteamWorkshopItemTag.CompositeId> {
    List<SteamWorkshopItemTag> findByInternalId(long internalId);

    List<SteamWorkshopItemTag> findByTagId(long tagId);
}