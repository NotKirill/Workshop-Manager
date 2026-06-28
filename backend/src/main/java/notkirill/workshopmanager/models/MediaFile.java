package notkirill.workshopmanager.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "media_files")
public class MediaFile {
    @Id
    @NotNull
    @Column(name = "media_item_id", nullable = false)
    private long mediaItemId;

    @Nullable
    @Column(name = "source_id", nullable = true)
    private String sourceId;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private int fileSize;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    protected MediaFile() {
        // Required by JPA
    }

    public MediaFile(long mediaItemId, @Nullable String sourceId, int fileSize, String fileName, String s3Key, JsonNode metadata) {
        this.mediaItemId = mediaItemId;
        this.sourceId = sourceId;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.s3Key = s3Key;
        this.metadata = metadata;
    }

    public long getMediaItemId() {
        return mediaItemId;
    }

    public void setMediaItemId(long mediaItemId) {
        this.mediaItemId = mediaItemId;
    }

    @Nullable
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(@Nullable String sourceId) {
        this.sourceId = sourceId;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "mediaItemId=" + mediaItemId +
                ", sourceId='" + sourceId + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", s3Key='" + s3Key + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
