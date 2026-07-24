package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaLink;
import notkirill.workshopmanager.models.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaLinkRepository extends JpaRepository<MediaLink, Long> {
    List<MediaLink> findByEntityAndEntityId(EntityType entity, long entityId);

    List<MediaLink> findByPurpose(String purpose);
}