package notkirill.workshopmanager.modspace.steam.repositories;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SteamWorkshopTagRepository extends JpaRepository<SteamWorkshopTag, Long> {
    Optional<SteamWorkshopTag> findByTagKeyAndConsumerAppId(String tagKey, long consumerAppId);

    List<SteamWorkshopTag> findByTagIdIn(Collection<Long> tagIds);
}
