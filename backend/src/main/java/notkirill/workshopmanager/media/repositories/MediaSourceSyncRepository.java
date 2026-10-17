package notkirill.workshopmanager.media.repositories;

import notkirill.workshopmanager.media.models.MediaSourceSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface MediaSourceSyncRepository extends JpaRepository<MediaSourceSync, MediaSourceSync.CompositeId> {
    @Query(nativeQuery = true, value = """
            SELECT * FROM media_source_syncs
            WHERE due_at <= :now AND failures < :maxFailures
            ORDER BY due_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED""")
    List<MediaSourceSync> claimDue(OffsetDateTime now, short maxFailures, int limit);

    List<MediaSourceSync> findByFailuresGreaterThanEqualOrderByDueAtDesc(short failures);

    long countByFailuresGreaterThanEqual(short failures);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO media_source_syncs (entity, entity_id, due_at, source_modspace, source_original_id)
            VALUES (CAST(:entity AS entity_type), :entityId, :now, CAST(:modspace AS modspace_type), :originalId)
            ON CONFLICT (entity, entity_id) DO UPDATE
            SET due_at = :now, failures = 0, source_modspace = EXCLUDED.source_modspace,
                source_original_id = EXCLUDED.source_original_id""")
    void requestNow(String entity, long entityId, String modspace, String originalId, OffsetDateTime now);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO media_source_syncs (entity, entity_id, due_at, source_modspace, source_original_id)
            VALUES (CAST(:entity AS entity_type), :entityId, :now, CAST(:modspace AS modspace_type), :originalId)
            ON CONFLICT (entity, entity_id) DO UPDATE
            SET source_modspace = EXCLUDED.source_modspace, source_original_id = EXCLUDED.source_original_id""")
    void createIfMissing(String entity, long entityId, String modspace, String originalId, OffsetDateTime now);
}
