package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemContentRepository extends JpaRepository<ItemContent, Long> {
    List<ItemContent> findByInternalMetadataId(long internalMetadataId);
}