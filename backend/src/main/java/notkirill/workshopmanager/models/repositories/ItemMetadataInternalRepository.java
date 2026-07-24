package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ItemMetadataInternal;
import notkirill.workshopmanager.models.enums.ModspaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemMetadataInternalRepository extends JpaRepository<ItemMetadataInternal, Long> {
    Optional<ItemMetadataInternal> findBySourceModspaceAndGameIdAndOriginalId(ModspaceType sourceModspace, long gameId,
                                                                              String originalId);

    List<ItemMetadataInternal> findByTimeRegisteredBetween(OffsetDateTime timeRegisteredStart,
                                                           OffsetDateTime timeRegisteredEnd);

    List<ItemMetadataInternal> findByTimeRegisteredLessThan(OffsetDateTime timeRegistered);

    List<ItemMetadataInternal> findByTimeRegisteredLessThanEqual(OffsetDateTime timeRegistered);

    List<ItemMetadataInternal> findByTimeRegisteredGreaterThan(OffsetDateTime timeRegistered);

    List<ItemMetadataInternal> findByTimeRegisteredGreaterThanEqual(OffsetDateTime timeRegistered);

    List<ItemMetadataInternal> findByTimeUpdatedBetween(OffsetDateTime timeUpdatedStart, OffsetDateTime timeUpdatedEnd);

    List<ItemMetadataInternal> findByTimeUpdatedLessThan(OffsetDateTime timeUpdated);

    List<ItemMetadataInternal> findByTimeUpdatedLessThanEqual(OffsetDateTime timeUpdated);

    List<ItemMetadataInternal> findByTimeUpdatedGreaterThan(OffsetDateTime timeUpdated);

    List<ItemMetadataInternal> findByTimeUpdatedGreaterThanEqual(OffsetDateTime timeUpdated);
}