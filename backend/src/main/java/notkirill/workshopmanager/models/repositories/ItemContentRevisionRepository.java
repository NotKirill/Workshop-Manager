package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemContentRevision;
import notkirill.workshopmanager.models.idClasses.ItemContentRevisionComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemContentRevisionRepository extends JpaRepository<ItemContentRevision, ItemContentRevisionComplexKey> {
    Optional<ItemContentRevision> findByItemContentId(long itemContentId);
}