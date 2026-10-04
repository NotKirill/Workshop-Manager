package notkirill.workshopmanager.modspace.steam.repositories;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SteamWorkshopTagRepository extends JpaRepository<SteamWorkshopTag, Long> {
    List<SteamWorkshopTag> findByTagKeyAndConsumerAppId(String tagKey, long consumerAppId);
}