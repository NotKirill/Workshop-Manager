package notkirill.workshopmanager.content.repositories;

import notkirill.workshopmanager.content.models.ItemContentRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemContentRevisionRepository extends JpaRepository<ItemContentRevision, ItemContentRevision.CompositeId> {
    List<ItemContentRevision> findByItemContentIdOrderByRevisionDesc(long itemContentId);

    List<ItemContentRevision> findByItemContentIdIn(Collection<Long> itemContentIds);

    Optional<ItemContentRevision> findByItemContentIdAndRevision(long itemContentId, long revision);

    Optional<ItemContentRevision> findFirstByItemContentIdAndUploadedByUserIdIsNullOrderByRevisionDesc(
            long itemContentId);

    List<ItemContentRevision> findByItemContentIdAndUploadedByUserId(long itemContentId, long uploadedByUserId);

    @Query("SELECT COALESCE(MAX(r.revision), 0) FROM ItemContentRevision r WHERE r.itemContentId = :contentId")
    long findMaxRevision(long contentId);

    long countByItemContentId(long itemContentId);
}
