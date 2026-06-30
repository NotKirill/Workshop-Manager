package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemMetadataPersonal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemMetadataPersonalRepository extends JpaRepository<ItemMetadataPersonal, Long> {
    Optional<ItemMetadataPersonal> findByInternalMetadataId(long internalMetadataId);

    Optional<ItemMetadataPersonal> findByInternalMetadataIdAndUserId(long internalMetadataId, long userId);

    ItemMetadataPersonal findByContentImport(boolean contentImport);
}