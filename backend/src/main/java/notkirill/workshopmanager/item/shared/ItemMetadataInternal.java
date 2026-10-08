package notkirill.workshopmanager.item.shared;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "item_metadata_internal",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_item_metadata_internal_identity", columnNames = {"source_modspace", "game_id", "original_id"})
        })
@EntityListeners(AuditingEntityListener.class)
public class ItemMetadataInternal {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "source_modspace", columnDefinition = "modspace_type", nullable = false)
    private ModspaceType sourceModspace;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private long gameId;

    @NotNull
    @Column(name = "original_id", nullable = false)
    private String originalId;

    @NotNull
    @Column(name = "time_registered", nullable = false)
    @CreatedDate
    private OffsetDateTime timeRegistered;

    @Nullable
    @Column(name = "time_updated", nullable = true)
    private OffsetDateTime timeUpdated;

    @Nullable
    @Column(name = "time_synced", nullable = true)
    private OffsetDateTime timeSynced;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "source_status", columnDefinition = "item_source_status", nullable = false)
    private ItemSourceStatus sourceStatus = ItemSourceStatus.AVAILABLE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "lifecycle_state", columnDefinition = "item_lifecycle_state", nullable = false)
    private ItemLifecycleState lifecycleState = ItemLifecycleState.ACTIVE;

    protected ItemMetadataInternal() {
        // Required by JPA
    }

    public ItemMetadataInternal(ModspaceType sourceModspace, long gameId, String originalId) {
        this.sourceModspace = sourceModspace;
        this.gameId = gameId;
        this.originalId = originalId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ModspaceType getSourceModspace() {
        return sourceModspace;
    }

    public void setSourceModspace(ModspaceType sourceModspace) {
        this.sourceModspace = sourceModspace;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public OffsetDateTime getTimeRegistered() {
        return timeRegistered;
    }

    public OffsetDateTime getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(OffsetDateTime timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public OffsetDateTime getTimeSynced() {
        return timeSynced;
    }

    public void setTimeSynced(OffsetDateTime timeSynced) {
        this.timeSynced = timeSynced;
    }

    public ItemSourceStatus getSourceStatus() {
        return sourceStatus;
    }

    public void setSourceStatus(ItemSourceStatus sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public ItemLifecycleState getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(ItemLifecycleState lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    @Override
    public String toString() {
        return "ItemMetadataInternal{" +
                "id=" + id +
                ", sourceModspace=" + sourceModspace +
                ", gameId=" + gameId +
                ", originalId='" + originalId + '\'' +
                ", timeRegistered=" + timeRegistered +
                ", timeUpdated=" + timeUpdated +
                ", timeSynced=" + timeSynced +
                ", sourceStatus=" + sourceStatus +
                ", lifecycleState=" + lifecycleState +
                '}';
    }
}
