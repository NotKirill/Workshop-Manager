package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaItem;
import notkirill.workshopmanager.models.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    List<MediaItem> findByType(MediaType type);

    List<MediaItem> findByOwnerUserId(Long ownerUserId);
}