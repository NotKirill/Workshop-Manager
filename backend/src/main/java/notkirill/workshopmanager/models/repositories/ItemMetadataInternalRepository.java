package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemMetadataInternal;
import notkirill.workshopmanager.models.enums.ModspaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ItemMetadataInternalRepository extends JpaRepository<ItemMetadataInternal, Long> {
    Optional<ItemMetadataInternal> findBySourceModspaceAndGameIdAndOriginalId(ModspaceType sourceModspace, long gameId, String originalId);

    Optional<ItemMetadataInternal> findByTimeRegisteredBetween(OffsetDateTime timeRegisteredStart, OffsetDateTime timeRegisteredEnd);

    Optional<ItemMetadataInternal> findByTimeRegisteredLessThan(OffsetDateTime timeRegistered);

    Optional<ItemMetadataInternal> findByTimeRegisteredLessThanEqual(OffsetDateTime timeRegistered);

    Optional<ItemMetadataInternal> findByTimeRegisteredGreaterThan(OffsetDateTime timeRegistered);

    Optional<ItemMetadataInternal> findByTimeRegisteredGreaterThanEqual(OffsetDateTime timeRegistered);

    Optional<ItemMetadataInternal> findByTimeUpdatedBetween(OffsetDateTime timeUpdatedStart, OffsetDateTime timeUpdatedEnd);

    Optional<ItemMetadataInternal> findByTimeUpdatedLessThan(OffsetDateTime timeUpdated);

    Optional<ItemMetadataInternal> findByTimeUpdatedLessThanEqual(OffsetDateTime timeUpdated);

    Optional<ItemMetadataInternal> findByTimeUpdatedGreaterThan(OffsetDateTime timeUpdated);

    Optional<ItemMetadataInternal> findByTimeUpdatedGreaterThanEqual(OffsetDateTime timeUpdated);
}