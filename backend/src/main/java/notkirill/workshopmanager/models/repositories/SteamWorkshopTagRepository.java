package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.SteamWorkshopTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SteamWorkshopTagRepository extends JpaRepository<SteamWorkshopTag, Long> {
    Optional<SteamWorkshopTag> findByTagAndConsumerAppId(String tag, long consumerAppId);
}