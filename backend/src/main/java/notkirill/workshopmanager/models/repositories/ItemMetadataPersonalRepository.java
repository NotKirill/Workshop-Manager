package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemMetadataPersonal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemMetadataPersonalRepository extends JpaRepository<ItemMetadataPersonal, Long> {
    List<ItemMetadataPersonal> findByInternalMetadataId(long internalMetadataId);

    Optional<ItemMetadataPersonal> findByInternalMetadataIdAndUserId(long internalMetadataId, long userId);

    List<ItemMetadataPersonal> findByContentImport(boolean contentImport);
}