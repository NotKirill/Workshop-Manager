package notkirill.workshopmanager.content.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@Table(name = "content_uploads")
@EntityListeners(AuditingEntityListener.class)
public class ContentUpload {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "internal_metadata_id", nullable = false)
    private long internalMetadataId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "size", nullable = false)
    private long size;

    @NotNull
    @Column(name = "received", nullable = false)
    private long received;

    @NotNull
    @Column(name = "time_created", nullable = false)
    @CreatedDate
    private OffsetDateTime timeCreated;

    @NotNull
    @Column(name = "time_updated", nullable = false)
    @LastModifiedDate
    private OffsetDateTime timeUpdated;

    protected ContentUpload() {
        // Required by JPA
    }

    public ContentUpload(long internalMetadataId, long userId, String fileName, long size) {
        this.internalMetadataId = internalMetadataId;
        this.userId = userId;
        this.fileName = fileName;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public long getInternalMetadataId() {
        return internalMetadataId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }

    @Override
    public String toString() {
        return "ContentUpload{" +
                "id=" + id +
                ", internalMetadataId=" + internalMetadataId +
                ", userId=" + userId +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", received=" + received +
                ", timeCreated=" + timeCreated +
                ", timeUpdated=" + timeUpdated +
                '}';
    }
}
