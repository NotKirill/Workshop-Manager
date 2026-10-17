package notkirill.workshopmanager.content.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.content.enums.ContentJobError;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.enums.ContentJobType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "content_jobs")
public class ContentJob {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "internal_metadata_id", nullable = false)
    private long internalMetadataId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "content_job_type", nullable = false)
    private ContentJobType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "content_job_status", nullable = false)
    private ContentJobStatus status = ContentJobStatus.PENDING;

    @Column(name = "revision")
    private Long revision;

    @Column(name = "requested_by_user_id")
    private Long requestedByUserId;

    @Column(name = "attempts", nullable = false)
    private short attempts;

    @NotNull
    @Column(name = "next_attempt_at", nullable = false)
    private OffsetDateTime nextAttemptAt;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_code")
    private ContentJobError errorCode;

    @NotNull
    @Column(name = "time_created", nullable = false, updatable = false)
    private OffsetDateTime timeCreated;

    @Column(name = "time_started")
    private OffsetDateTime timeStarted;

    @Column(name = "time_finished")
    private OffsetDateTime timeFinished;

    protected ContentJob() {
        // Required by JPA
    }

    public ContentJob(long internalMetadataId, ContentJobType type, Long revision, Long requestedByUserId,
                      OffsetDateTime now, OffsetDateTime nextAttemptAt) {
        this.internalMetadataId = internalMetadataId;
        this.type = type;
        this.revision = revision;
        this.requestedByUserId = requestedByUserId;
        this.timeCreated = now;
        this.nextAttemptAt = nextAttemptAt;
    }

    public long getId() {
        return id;
    }

    public long getInternalMetadataId() {
        return internalMetadataId;
    }

    public ContentJobType getType() {
        return type;
    }

    public ContentJobStatus getStatus() {
        return status;
    }

    public void setStatus(ContentJobStatus status) {
        this.status = status;
    }

    public Long getRevision() {
        return revision;
    }

    public Long getRequestedByUserId() {
        return requestedByUserId;
    }

    public short getAttempts() {
        return attempts;
    }

    public void setAttempts(short attempts) {
        this.attempts = attempts;
    }

    public OffsetDateTime getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(OffsetDateTime nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ContentJobError getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ContentJobError errorCode) {
        this.errorCode = errorCode;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    public OffsetDateTime getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(OffsetDateTime timeStarted) {
        this.timeStarted = timeStarted;
    }

    public OffsetDateTime getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(OffsetDateTime timeFinished) {
        this.timeFinished = timeFinished;
    }

    @Override
    public String toString() {
        return "ContentJob{" +
                "id=" + id +
                ", internalMetadataId=" + internalMetadataId +
                ", type=" + type +
                ", status=" + status +
                ", revision=" + revision +
                ", attempts=" + attempts +
                ", nextAttemptAt=" + nextAttemptAt +
                '}';
    }
}
