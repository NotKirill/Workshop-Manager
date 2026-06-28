package notkirill.workshopmanager.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.MediaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "media_items")
public class MediaItem {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "media_type", nullable = false)
    private MediaType type;

    @NotNull
    @Column(name = "time_uploaded", nullable = false)
    private OffsetDateTime timeUploaded = OffsetDateTime.now();

    @Nullable
    @Column(name = "owner_user_id", nullable = true)
    private Long ownerUserId;

    protected MediaItem() {
        // Required by JPA
    }

    public MediaItem(MediaType type, OffsetDateTime timeUploaded, @Nullable Long ownerUserId) {
        this.type = type;
        this.timeUploaded = timeUploaded;
        this.ownerUserId = ownerUserId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public OffsetDateTime getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(OffsetDateTime timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    @Nullable
    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(@Nullable Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "id=" + id +
                ", type=" + type +
                ", timeUploaded=" + timeUploaded +
                ", ownerUserId=" + ownerUserId +
                '}';
    }
}
