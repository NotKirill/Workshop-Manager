package notkirill.workshopmanager.item.lifecycle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "item_cleanups")
public class ItemCleanup {
    @Id
    @NotNull
    @Column(name = "internal_metadata_id", nullable = false)
    private long internalMetadataId;

    @NotNull
    @Column(name = "orphaned_at", nullable = false)
    private OffsetDateTime orphanedAt;

    @NotNull
    @Column(name = "attempts", nullable = false)
    private short attempts;

    @Column(name = "next_attempt_at")
    private OffsetDateTime nextAttemptAt;

    @Column(name = "last_error")
    private String lastError;

    protected ItemCleanup() {
        // Required by JPA
    }

    public ItemCleanup(long internalMetadataId, OffsetDateTime orphanedAt) {
        this.internalMetadataId = internalMetadataId;
        this.orphanedAt = orphanedAt;
    }

    public long getInternalMetadataId() {
        return internalMetadataId;
    }

    public OffsetDateTime getOrphanedAt() {
        return orphanedAt;
    }

    public short getAttempts() {
        return attempts;
    }

    public void setAttempts(short attempts) {
        this.attempts = attempts;
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

    @Override
    public String toString() {
        return "ItemCleanup{" +
                "internalMetadataId=" + internalMetadataId +
                ", orphanedAt=" + orphanedAt +
                ", attempts=" + attempts +
                ", nextAttemptAt=" + nextAttemptAt +
                ", lastError='" + lastError + '\'' +
                '}';
    }
}
