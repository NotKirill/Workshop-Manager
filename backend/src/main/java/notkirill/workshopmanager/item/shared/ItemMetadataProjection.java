package notkirill.workshopmanager.item.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "item_metadata_projection")
public class ItemMetadataProjection {
    @Id
    @NotNull
    @Column(name = "internal_id", nullable = false)
    private long internalId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "source_time_created")
    private OffsetDateTime sourceTimeCreated;

    protected ItemMetadataProjection() {
        // Required by JPA
    }

    public ItemMetadataProjection(long internalId) {
        this.internalId = internalId;
    }

    public long getInternalId() {
        return internalId;
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public OffsetDateTime getSourceTimeCreated() {
        return sourceTimeCreated;
    }

    public void setSourceTimeCreated(OffsetDateTime sourceTimeCreated) {
        this.sourceTimeCreated = sourceTimeCreated;
    }

    @Override
    public String toString() {
        return "ItemMetadataProjection{" +
                "internalId=" + internalId +
                ", title='" + title + '\'' +
                '}';
    }
}
