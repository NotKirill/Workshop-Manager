package notkirill.workshopmanager.media.repositories;

import notkirill.workshopmanager.media.models.MediaItem;
import notkirill.workshopmanager.media.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    List<MediaItem> findByType(MediaType type);

    List<MediaItem> findByOwnerUserId(Long ownerUserId);
}