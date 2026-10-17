package notkirill.workshopmanager.content;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.common.Texts;
import notkirill.workshopmanager.content.enums.ContentJobError;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.enums.ContentJobType;
import notkirill.workshopmanager.content.models.ContentJob;
import notkirill.workshopmanager.content.repositories.ContentJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ContentJobService {
    private static final Logger log = LoggerFactory.getLogger(ContentJobService.class);

    private static final Set<ContentJobStatus> ACTIVE_STATUSES =
            EnumSet.of(ContentJobStatus.PENDING, ContentJobStatus.RUNNING);
    private static final Set<ContentJobStatus> FINISHED_STATUSES =
            EnumSet.of(ContentJobStatus.SUCCEEDED, ContentJobStatus.CANCELLED);

    private final ContentJobRepository repository;
    private final ContentProperties properties;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    @Autowired
    public ContentJobService(ContentJobRepository repository, ContentProperties properties,
                             PlatformTransactionManager transactionManager, Clock clock) {
        this.repository = repository;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    @Transactional
    public void requestBackup(long internalId, @Nullable Long requestedByUserId) {
        if (repository.findFirstByInternalMetadataIdAndTypeAndStatusIn(
                internalId, ContentJobType.BACKUP, ACTIVE_STATUSES).isPresent()) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now(clock);
        try {
            repository.saveAndFlush(new ContentJob(
                    internalId, ContentJobType.BACKUP, null, requestedByUserId,
                    now, now
            ));
        } catch (DataIntegrityViolationException _) {
            log.debug("Backup of item {} already queued", internalId);
        }
    }

    @Transactional
    public void requestDeleteAll(long internalId, Long requestedByUserId) {
        if (repository.findFirstByInternalMetadataIdAndTypeAndStatusIn(
                internalId, ContentJobType.DELETE_ALL, ACTIVE_STATUSES).isPresent()) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now(clock);
        repository.save(new ContentJob(
                internalId, ContentJobType.DELETE_ALL, null, requestedByUserId, now,
                now
        ));
    }

    @Transactional
    public void requestDeleteRevision(long internalId, long revision, @Nullable Long requestedByUserId) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        repository.save(new ContentJob(
                internalId, ContentJobType.DELETE_REVISION, revision, requestedByUserId,
                now, now
        ));
    }

    @Transactional
    public void cancelPending(long internalId, String reason) {
        repository.changeStatus(
                internalId, ContentJobStatus.PENDING, ContentJobStatus.CANCELLED, reason,
                OffsetDateTime.now(clock)
        );
    }

    @Transactional
    public void deleteJobsOf(long internalId) {
        repository.deleteByInternalMetadataId(internalId);
    }

    @Transactional
    public int pruneFinished(Duration olderThan) {
        return repository.deleteFinished(FINISHED_STATUSES, OffsetDateTime.now(clock).minus(olderThan));
    }

    @Transactional(readOnly = true)
    public Optional<ContentJob> activeJob(long internalId) {
        return repository.findFirstByInternalMetadataIdAndStatusInOrderByIdDesc(internalId, ACTIVE_STATUSES);
    }

    @Transactional(readOnly = true)
    public Optional<ContentJob> lastFailedJob(long internalId) {
        return repository.findFirstByInternalMetadataIdAndStatusOrderByIdDesc(internalId, ContentJobStatus.FAILED);
    }

    @Transactional(readOnly = true)
    public Map<Long, ContentJob> activeJobs(Collection<Long> internalIds) {
        Map<Long, ContentJob> active = new HashMap<>();
        for (ContentJob job : repository.findByInternalMetadataIdInAndStatusIn(internalIds, ACTIVE_STATUSES)) {
            active.putIfAbsent(job.getInternalMetadataId(), job);
        }
        return active;
    }

    public Optional<ContentJob> claimNext() {
        return transactionTemplate.execute(status -> {
            OffsetDateTime now = OffsetDateTime.now(clock);
            Optional<ContentJob> job = repository.claimNext(now, now.minus(properties.jobs().runningTimeout()));
            job.ifPresent(claimed -> {
                claimed.setStatus(ContentJobStatus.RUNNING);
                claimed.setTimeStarted(now);
                claimed.setAttempts((short) (claimed.getAttempts() + 1));
            });
            return job;
        });
    }

    public void complete(long jobId, ContentJobStatus status, String note) {
        transactionTemplate.executeWithoutResult(tx -> repository.findById(jobId).ifPresent(job -> {
            OffsetDateTime now = OffsetDateTime.now(clock);
            job.setStatus(status);
            job.setTimeFinished(now);
            job.setNote(note);
            job.setLastError(null);
            job.setErrorCode(null);
            repository.save(job);
        }));
    }

    @Transactional(readOnly = true)
    public List<ContentJob> recentJobs(Collection<ContentJobStatus> statuses, int limit) {
        return repository.findByStatusInOrderByIdDesc(statuses, PageRequest.of(0, limit));
    }

    public long countByStatus(ContentJobStatus status) {
        return repository.countByStatus(status);
    }

    public void fail(ContentJob job, Exception error, ContentJobError errorCode, boolean retryable) {
        log.warn(
                "Content job {} ({}) failed, attempt {}: {}", job.getId(), job.getType(), job.getAttempts(),
                error.getMessage()
        );
        transactionTemplate.executeWithoutResult(tx -> repository.findById(job.getId()).ifPresent(current -> {
            OffsetDateTime now = OffsetDateTime.now(clock);
            current.setLastError(Texts.errorMessage(error));
            current.setErrorCode(errorCode);
            if (!retryable || current.getAttempts() >= properties.jobs().maxAttempts()) {
                current.setStatus(ContentJobStatus.FAILED);
                current.setTimeFinished(now);
                log.error("Content job {} moved to FAILED after {} attempts", current.getId(), current.getAttempts());
            } else {
                long factor = 1L << Math.min(10, current.getAttempts() - 1);
                Duration delay = properties.jobs().retryBaseDelay().multipliedBy(factor);
                current.setStatus(ContentJobStatus.PENDING);
                current.setNextAttemptAt(now.plus(delay));
            }
            repository.save(current);
        }));
    }

    public void retry(long jobId) {
        transactionTemplate.executeWithoutResult(tx -> repository.findById(jobId).ifPresent(job -> {
            if (job.getStatus() == ContentJobStatus.FAILED || job.getStatus() == ContentJobStatus.CANCELLED) {
                job.setStatus(ContentJobStatus.PENDING);
                job.setAttempts((short) 0);
                job.setNextAttemptAt(OffsetDateTime.now(clock));
                job.setTimeFinished(null);
                job.setNote(null);
                repository.save(job);
            }
        }));
    }
}
