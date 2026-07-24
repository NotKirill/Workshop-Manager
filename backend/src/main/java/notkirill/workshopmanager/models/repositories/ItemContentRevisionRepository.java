package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemContentRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemContentRevisionRepository extends JpaRepository<ItemContentRevision, ItemContentRevision.CompositeId> {
    List<ItemContentRevision> findByItemContentId(long itemContentId);
}