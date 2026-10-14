package notkirill.workshopmanager.media.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "media_files")
public class MediaFile {
    @Id
    @NotNull
    @Column(name = "media_item_id", nullable = false)
    private long mediaItemId;

    @Nullable
    @Column(name = "source_version", nullable = true)
    private String sourceVersion;

    @NotNull
    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    protected MediaFile() {
        // Required by JPA
    }

    public MediaFile(long mediaItemId, @Nullable String sourceVersion, long fileSize, String fileName, String s3Key) {
        this.mediaItemId = mediaItemId;
        this.sourceVersion = sourceVersion;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.s3Key = s3Key;
    }

    public long getMediaItemId() {
        return mediaItemId;
    }

    public void setMediaItemId(long mediaItemId) {
        this.mediaItemId = mediaItemId;
    }

    @Nullable
    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(@Nullable String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
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

    @Override
    public String toString() {
        return "MediaFile{" +
                "mediaItemId=" + mediaItemId +
                ", sourceVersion='" + sourceVersion + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", s3Key='" + s3Key + '\'' +
                '}';
    }
}
