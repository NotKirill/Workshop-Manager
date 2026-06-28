package notkirill.workshopmanager.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.MediaLinkOverrideComplexKey;

@Entity
@Table(name = "media_link_overrides")
@IdClass(MediaLinkOverrideComplexKey.class)
public class MediaLinkOverride {
    @Id
    @NotNull
    @Column(name = "link_id", nullable = false)
    private long linkId;

    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Nullable
    @Column(name = "media_item_id", nullable = true)
    private long mediaItemId;

    @Column(name = "sort_order")
    private int sortOrder;

    protected MediaLinkOverride() {
        // Required by JPA
    }

    public MediaLinkOverride(long linkId, long userId, long mediaItemId, int sortOrder) {
        this.linkId = linkId;
        this.userId = userId;
        this.mediaItemId = mediaItemId;
        this.sortOrder = sortOrder;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMediaItemId() {
        return mediaItemId;
    }

    public void setMediaItemId(long mediaItemId) {
        this.mediaItemId = mediaItemId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "MediaLinkOverride{" +
                "linkId=" + linkId +
                ", userId=" + userId +
                ", mediaItemId=" + mediaItemId +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
