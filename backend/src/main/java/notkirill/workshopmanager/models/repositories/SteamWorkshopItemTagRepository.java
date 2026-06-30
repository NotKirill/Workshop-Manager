package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.models.idClasses.SteamWorkshopItemTagComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SteamWorkshopItemTagRepository extends JpaRepository<SteamWorkshopItemTag, SteamWorkshopItemTagComplexKey> {
    Optional<SteamWorkshopItemTag> findByInternalId(long internalId);

    Optional<SteamWorkshopItemTag> findByTagId(long tagId);
}