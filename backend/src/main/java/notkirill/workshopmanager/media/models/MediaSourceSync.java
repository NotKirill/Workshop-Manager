package notkirill.workshopmanager.media.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "media_source_syncs")
@IdClass(MediaSourceSync.CompositeId.class)
public class MediaSourceSync {
    @Id
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "entity", columnDefinition = "entity_type", nullable = false)
    private EntityType entity;

    @Id
    @NotNull
    @Column(name = "entity_id", nullable = false)
    private long entityId;

    @Nullable
    @Column(name = "due_at", nullable = true)
    private OffsetDateTime dueAt;

    @NotNull
    @Column(name = "failures", nullable = false)
    private short failures;

    @Nullable
    @Column(name = "last_error", nullable = true)
    private String lastError;

    @Nullable
    @Column(name = "time_synced", nullable = true)
    private OffsetDateTime timeSynced;

    @Nullable
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "source_modspace", columnDefinition = "modspace_type", nullable = true)
    private ModspaceType sourceModspace;

    @Nullable
    @Column(name = "source_original_id", nullable = true)
    private String sourceOriginalId;

    protected MediaSourceSync() {
        // Required by JPA; rows are created by MediaSourceSyncRepository.request
    }

    public EntityType getEntity() {
        return entity;
    }

    public long getEntityId() {
        return entityId;
    }

    @Nullable
    public ModspaceType getSourceModspace() {
        return sourceModspace;
    }

    @Nullable
    public String getSourceOriginalId() {
        return sourceOriginalId;
    }

    @Nullable
    public OffsetDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(@Nullable OffsetDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public short getFailures() {
        return failures;
    }

    public void setFailures(short failures) {
        this.failures = failures;
    }

    @Nullable
    public String getLastError() {
        return lastError;
    }

    public void setLastError(@Nullable String lastError) {
        this.lastError = lastError;
    }

    @Nullable
    public OffsetDateTime getTimeSynced() {
        return timeSynced;
    }

    public void setTimeSynced(@Nullable OffsetDateTime timeSynced) {
        this.timeSynced = timeSynced;
    }

    @Override
    public String toString() {
        return "MediaSourceSync{" +
                "entity=" + entity +
                ", entityId=" + entityId +
                ", dueAt=" + dueAt +
                ", failures=" + failures +
                ", lastError='" + lastError + '\'' +
                ", timeSynced=" + timeSynced +
                '}';
    }

    public static class CompositeId implements Serializable {
        private EntityType entity;
        private long entityId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(EntityType entity, long entityId) {
            this.entity = entity;
            this.entityId = entityId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(entity, entityId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return entity == that.entity && entityId == that.entityId;
        }
    }
}
