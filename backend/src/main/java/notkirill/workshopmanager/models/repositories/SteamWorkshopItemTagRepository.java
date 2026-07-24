package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.models.idClasses.SteamWorkshopItemTagComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SteamWorkshopItemTagRepository extends JpaRepository<SteamWorkshopItemTag, SteamWorkshopItemTagComplexKey> {
    List<SteamWorkshopItemTag> findByInternalId(long internalId);

    List<SteamWorkshopItemTag> findByTagId(long tagId);
}