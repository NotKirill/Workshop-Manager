package notkirill.workshopmanager.content.repositories;

import jakarta.persistence.LockModeType;
import notkirill.workshopmanager.content.models.ItemContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemContentRepository extends JpaRepository<ItemContent, Long> {
    Optional<ItemContent> findByInternalMetadataId(long internalMetadataId);

    List<ItemContent> findByInternalMetadataIdIn(Collection<Long> internalMetadataIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ItemContent c WHERE c.internalMetadataId = :internalId")
    Optional<ItemContent> findByInternalMetadataIdForUpdate(long internalId);
}