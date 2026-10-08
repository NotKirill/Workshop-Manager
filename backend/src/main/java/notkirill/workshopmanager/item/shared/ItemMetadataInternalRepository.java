package notkirill.workshopmanager.item.shared;

import jakarta.persistence.LockModeType;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemMetadataInternalRepository extends JpaRepository<ItemMetadataInternal, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemMetadataInternal i WHERE i.id = :id")
    Optional<ItemMetadataInternal> findByIdForUpdate(long id);

    Optional<ItemMetadataInternal> findBySourceModspaceAndGameIdAndOriginalId(ModspaceType sourceModspace, long gameId,
                                                                              String originalId);

    Optional<ItemMetadataInternal> findFirstBySourceModspaceAndOriginalId(ModspaceType sourceModspace,
                                                                          String originalId);

    List<ItemMetadataInternal> findBySourceModspaceAndOriginalIdIn(ModspaceType sourceModspace,
                                                                   Collection<String> originalIds);

    long countByLifecycleState(ItemLifecycleState lifecycleState);

    List<ItemMetadataInternal> findByLifecycleStateOrderByIdAsc(ItemLifecycleState lifecycleState, Pageable pageable);

    @Query("""
            SELECT i.id FROM ItemMetadataInternal i
            WHERE i.lifecycleState = :state
              AND NOT EXISTS (SELECT 1 FROM ItemMetadataPersonal p WHERE p.internalMetadataId = i.id)
            ORDER BY i.id
            """)
    List<Long> findUnreferencedIds(ItemLifecycleState state, Pageable pageable);

    @Query(value = """
            SELECT i.* FROM item_metadata_internal i
            JOIN item_cleanups c ON c.internal_metadata_id = i.id
            WHERE ((i.lifecycle_state = 'ORPHANED' AND c.orphaned_at < :orphanedBefore)
                   OR i.lifecycle_state = 'DELETING')
              AND (c.next_attempt_at IS NULL OR c.next_attempt_at <= :now)
            ORDER BY i.id
            LIMIT :limit
            FOR UPDATE OF i SKIP LOCKED
            """, nativeQuery = true)
    List<ItemMetadataInternal> claimCleanupCandidates(OffsetDateTime orphanedBefore, OffsetDateTime now, int limit);

    @Query("""
            SELECT i FROM ItemMetadataInternal i
            WHERE i.sourceModspace = :modspace
              AND i.lifecycleState = :state
              AND (i.timeSynced IS NULL OR i.timeSynced < :syncedBefore)
            ORDER BY i.timeSynced ASC NULLS FIRST
            """)
    List<ItemMetadataInternal> findBySyncState(ModspaceType modspace, ItemLifecycleState state,
                                               OffsetDateTime syncedBefore, Pageable pageable);
}
