package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaLink;
import notkirill.workshopmanager.models.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaLinkRepository extends JpaRepository<MediaLink, Long> {
    Optional<MediaLink> findByEntityAndEntityId(EntityType entity, long entityId);

    Optional<MediaLink> findByPurpose(String purpose);
}