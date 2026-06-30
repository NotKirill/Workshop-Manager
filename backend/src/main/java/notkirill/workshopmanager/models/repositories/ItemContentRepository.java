package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemContentRepository extends JpaRepository<ItemContent, Long> {
    Optional<ItemContent> findByInternalMetadataId(long internalMetadataId);
}