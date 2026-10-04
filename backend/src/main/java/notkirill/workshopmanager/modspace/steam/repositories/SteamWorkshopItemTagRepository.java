package notkirill.workshopmanager.modspace.steam.repositories;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItemTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SteamWorkshopItemTagRepository extends JpaRepository<SteamWorkshopItemTag, SteamWorkshopItemTag.CompositeId> {
    List<SteamWorkshopItemTag> findByLocalId(long localId);

    List<SteamWorkshopItemTag> findByTagId(long tagId);

    // For skipping Hibernate persistence context as optimizing measure
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM SteamWorkshopItemTag t WHERE t.localId = :localId")
    int deleteByLocalId(long localId);
}
