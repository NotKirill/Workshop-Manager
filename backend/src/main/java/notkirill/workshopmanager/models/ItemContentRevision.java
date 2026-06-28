package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.ItemContentRevisionComplexKey;

import java.time.OffsetDateTime;

@Entity
@Table(name = "item_content_revisions")
@IdClass(ItemContentRevisionComplexKey.class)
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
    private int fileSize;

    @NotNull
    @Column(name = "time_created", nullable = false)
    private OffsetDateTime timeCreated = OffsetDateTime.now();

    protected ItemContentRevision() {
        // Required by JPA
    }

    public ItemContentRevision(long itemContentId, long revision, String s3Key, String fileName, int fileSize, OffsetDateTime timeCreated) {
        this.itemContentId = itemContentId;
        this.revision = revision;
        this.s3Key = s3Key;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.timeCreated = timeCreated;
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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(OffsetDateTime timeCreated) {
        this.timeCreated = timeCreated;
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
}
