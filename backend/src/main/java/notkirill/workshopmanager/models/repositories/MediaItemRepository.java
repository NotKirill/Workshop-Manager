package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaItem;
import notkirill.workshopmanager.models.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    Optional<MediaItem> findByType(MediaType type);

    Optional<MediaItem> findByOwnerUserId(Long ownerUserId);
}