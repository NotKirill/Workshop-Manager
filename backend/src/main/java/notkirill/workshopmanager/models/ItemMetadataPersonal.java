package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

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

    // TODO rename
    @NotNull
    @Column(name = "metadata_only", nullable = false)
    private boolean metadataOnly = true;

    // TODO rename
    @NotNull
    @Column(name = "content_import", nullable = false)
    private boolean contentImport = false;

    protected ItemMetadataPersonal() {
        // Required by JPA
    }

    public ItemMetadataPersonal(long userId, long internalMetadataId, String title, String description, String userCommentary, boolean metadataOnly, boolean contentImport) {
        this.userId = userId;
        this.internalMetadataId = internalMetadataId;
        this.title = title;
        this.description = description;
        this.userCommentary = userCommentary;
        this.metadataOnly = metadataOnly;
        this.contentImport = contentImport;
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

    public boolean isMetadataOnly() {
        return metadataOnly;
    }

    public void setMetadataOnly(boolean metadataOnly) {
        this.metadataOnly = metadataOnly;
    }

    public boolean isContentImport() {
        return contentImport;
    }

    public void setContentImport(boolean contentImport) {
        this.contentImport = contentImport;
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
                ", metadataOnly=" + metadataOnly +
                ", contentImport=" + contentImport +
                '}';
    }
}
