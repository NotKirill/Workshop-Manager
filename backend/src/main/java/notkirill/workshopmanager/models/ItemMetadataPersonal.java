package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.StoringStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "item_metadata_personal",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_item_metadata_personal_identity", columnNames = {"user_id", "internal_metadata_id"})
        })
public class ItemMetadataPersonal {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @NotNull
    @Column(name = "internal_metadata_id", nullable = false)
    private long internalMetadataId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "user_commentary")
    private String userCommentary;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "storing_mode", columnDefinition = "storing_strategy", nullable = false)
    private StoringStrategy storingMode = StoringStrategy.ONLY_METADATA;

    protected ItemMetadataPersonal() {
        // Required by JPA
    }

    public ItemMetadataPersonal(long userId, long internalMetadataId, String title, String description,
                                String userCommentary, StoringStrategy storingMode) {
        this.userId = userId;
        this.internalMetadataId = internalMetadataId;
        this.title = title;
        this.description = description;
        this.userCommentary = userCommentary;
        this.storingMode = storingMode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getInternalMetadataId() {
        return internalMetadataId;
    }

    public void setInternalMetadataId(long internalMetadataId) {
        this.internalMetadataId = internalMetadataId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserCommentary() {
        return userCommentary;
    }

    public void setUserCommentary(String userCommentary) {
        this.userCommentary = userCommentary;
    }

    public StoringStrategy getStoringMode() {
        return storingMode;
    }

    public void setStoringMode(StoringStrategy storingMode) {
        this.storingMode = storingMode;
    }

    @Override
    public String toString() {
        return "ItemMetadataPersonal{" +
                "id=" + id +
                ", userId=" + userId +
                ", internalMetadataId=" + internalMetadataId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userCommentary='" + userCommentary + '\'' +
                ", storingMode=" + storingMode +
                '}';
    }
}
