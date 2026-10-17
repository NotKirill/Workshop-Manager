package notkirill.workshopmanager.content.repositories;

import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.enums.ContentJobType;
import notkirill.workshopmanager.content.models.ContentJob;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ContentJobRepository extends JpaRepository<ContentJob, Long> {
    @Query(value = """
            SELECT * FROM content_jobs
            WHERE (status = 'PENDING' AND next_attempt_at <= :now)
               OR (status = 'RUNNING' AND time_started < :staleBefore)
            ORDER BY next_attempt_at, id
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<ContentJob> claimNext(OffsetDateTime now, OffsetDateTime staleBefore);

    Optional<ContentJob> findFirstByInternalMetadataIdAndTypeAndStatusIn(long internalMetadataId,
                                                                         ContentJobType type,
                                                                         Collection<ContentJobStatus> statuses);

    Optional<ContentJob> findFirstByInternalMetadataIdAndStatusInOrderByIdDesc(long internalMetadataId,
                                                                               Collection<ContentJobStatus> statuses);

    Optional<ContentJob> findFirstByInternalMetadataIdAndStatusOrderByIdDesc(long internalMetadataId,
                                                                             ContentJobStatus status);

    List<ContentJob> findByInternalMetadataIdInAndStatusIn(Collection<Long> internalMetadataIds,
                                                           Collection<ContentJobStatus> statuses);

    List<ContentJob> findByStatusInOrderByIdDesc(Collection<ContentJobStatus> statuses, Pageable pageable);

    long countByStatus(ContentJobStatus status);

    @Modifying
    @Query("""
            UPDATE ContentJob j SET j.status = :newStatus, j.note = :note, j.timeFinished = :now
            WHERE j.internalMetadataId = :internalId AND j.status = :currentStatus
            """)
    int changeStatus(long internalId, ContentJobStatus currentStatus, ContentJobStatus newStatus, String note,
                     OffsetDateTime now);

    @Modifying
    @Query("DELETE FROM ContentJob j WHERE j.internalMetadataId = :internalId")
    int deleteByInternalMetadataId(long internalId);

    @Modifying
    @Query("DELETE FROM ContentJob j WHERE j.status IN :statuses AND j.timeFinished < :before")
    int deleteFinished(Collection<ContentJobStatus> statuses, OffsetDateTime before);
}
