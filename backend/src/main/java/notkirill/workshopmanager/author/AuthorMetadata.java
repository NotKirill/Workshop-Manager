package notkirill.workshopmanager.author;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "author_metadata",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_author_metadata_identity", columnNames = {"source_modspace", "original_id"})
        })
public class AuthorMetadata {
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
    @Column(name = "original_id", nullable = false)
    private String originalId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "time_synced")
    private OffsetDateTime timeSynced;

    @Column(name = "refresh_requested_at")
    private OffsetDateTime refreshRequestedAt;

    protected AuthorMetadata() {
        // Required by JPA
    }

    public AuthorMetadata(ModspaceType sourceModspace,
                          String originalId,
                          String name) {
        this.sourceModspace = sourceModspace;
        this.originalId = originalId;
        this.name = name;
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

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getTimeSynced() {
        return timeSynced;
    }

    public void setTimeSynced(OffsetDateTime timeSynced) {
        this.timeSynced = timeSynced;
    }

    public OffsetDateTime getRefreshRequestedAt() {
        return refreshRequestedAt;
    }

    public void setRefreshRequestedAt(OffsetDateTime refreshRequestedAt) {
        this.refreshRequestedAt = refreshRequestedAt;
    }

    @Override
    public String toString() {
        return "AuthorMetadata{" +
                "id=" + id +
                ", sourceModspace=" + sourceModspace +
                ", originalId='" + originalId + '\'' +
                ", name='" + name + '\'' +
                ", timeSynced=" + timeSynced +
                ", refreshRequestedAt=" + refreshRequestedAt +
                '}';
    }
}
