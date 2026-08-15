package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.ModspaceType;
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

    @NotNull
    @Column(name = "time_updated", nullable = false)
    private OffsetDateTime timeUpdated = OffsetDateTime.now();

    protected ItemMetadataInternal() {
        // Required by JPA
    }

    public ItemMetadataInternal(ModspaceType sourceModspace, long gameId, String originalId,
                                OffsetDateTime timeUpdated) {
        this.sourceModspace = sourceModspace;
        this.gameId = gameId;
        this.originalId = originalId;
        this.timeUpdated = timeUpdated;
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

    @Override
    public String toString() {
        return "ItemMetadataInternal{" +
                "id=" + id +
                ", sourceModspace=" + sourceModspace +
                ", gameId=" + gameId +
                ", originalId='" + originalId + '\'' +
                ", timeRegistered=" + timeRegistered +
                ", timeUpdated=" + timeUpdated +
                '}';
    }
}
