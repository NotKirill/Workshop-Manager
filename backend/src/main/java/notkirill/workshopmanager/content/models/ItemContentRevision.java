package notkirill.workshopmanager.content.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "item_content_revisions")
@IdClass(ItemContentRevision.CompositeId.class)
@EntityListeners(AuditingEntityListener.class)
public class ItemContentRevision {
    @Id
    @NotNull
    @Column(name = "item_content_id", nullable = false)
    private long itemContentId;

    @Id
    @NotNull
    @Column(name = "revision", nullable = false)
    private long revision;

    @NotNull
    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "sha256")
    private String sha256;

    @Column(name = "source_version")
    private String sourceVersion;

    @Nullable
    @Column(name = "uploaded_by_user_id", nullable = true)
    private Long uploadedByUserId;

    @NotNull
    @Column(name = "time_created", nullable = false)
    @CreatedDate
    private OffsetDateTime timeCreated;

    protected ItemContentRevision() {
        // Required by JPA
    }

    public ItemContentRevision(long itemContentId, long revision, String s3Key, String fileName, long fileSize,
                               String sha256, String sourceVersion, @Nullable Long uploadedByUserId) {
        this.itemContentId = itemContentId;
        this.revision = revision;
        this.s3Key = s3Key;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.sha256 = sha256;
        this.sourceVersion = sourceVersion;
        this.uploadedByUserId = uploadedByUserId;
    }

    public String getSha256() {
        return sha256;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    @Nullable
    public Long getUploadedByUserId() {
        return uploadedByUserId;
    }

    public boolean isVisibleTo(long userId) {
        return uploadedByUserId == null || uploadedByUserId == userId;
    }

    public long getItemContentId() {
        return itemContentId;
    }

    public void setItemContentId(long itemContentId) {
        this.itemContentId = itemContentId;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    @Override
    public String toString() {
        return "ItemContentRevision{" +
                "itemContentId=" + itemContentId +
                ", revision=" + revision +
                ", s3Key='" + s3Key + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", timeCreated=" + timeCreated +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long itemContentId;
        private long revision;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long itemContentId, long revision) {
            this.itemContentId = itemContentId;
            this.revision = revision;
        }

        public long getItemContentId() {
            return itemContentId;
        }

        public void setItemContentId(long itemContentId) {
            this.itemContentId = itemContentId;
        }

        public long getRevision() {
            return revision;
        }

        public void setRevision(long revision) {
            this.revision = revision;
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemContentId, revision);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return itemContentId == that.itemContentId && revision == that.revision;
        }

        @Override
        public String toString() {
            return "ItemContentRevision.CompositeId{" +
                    "itemContentId=" + itemContentId +
                    ", revision=" + revision +
                    '}';
        }
    }
}
