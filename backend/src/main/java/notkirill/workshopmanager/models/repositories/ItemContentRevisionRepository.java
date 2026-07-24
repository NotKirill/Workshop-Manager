package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemContentRevision;
import notkirill.workshopmanager.models.idClasses.ItemContentRevisionComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemContentRevisionRepository extends JpaRepository<ItemContentRevision, ItemContentRevisionComplexKey> {
    List<ItemContentRevision> findByItemContentId(long itemContentId);
}