package notkirill.workshopmanager.media.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "media_link_overrides")
@IdClass(MediaLinkOverride.CompositeId.class)
public class MediaLinkOverride {
    @Id
    @NotNull
    @Column(name = "link_id", nullable = false)
    private long linkId;

    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @NotNull
    @Column(name = "hidden", nullable = false)
    private boolean hidden;

    @Nullable
    @Column(name = "sort_order", nullable = true)
    private Integer sortOrder;

    protected MediaLinkOverride() {
        // Required by JPA
    }

    public MediaLinkOverride(long linkId, long userId, boolean hidden, @Nullable Integer sortOrder) {
        this.linkId = linkId;
        this.userId = userId;
        this.hidden = hidden;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Nullable
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(@Nullable Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "MediaLinkOverride{" +
                "linkId=" + linkId +
                ", userId=" + userId +
                ", hidden=" + hidden +
                ", sortOrder=" + sortOrder +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long linkId;
        private long userId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long linkId, long userId) {
            this.linkId = linkId;
            this.userId = userId;
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

        @Override
        public int hashCode() {
            return Objects.hash(linkId, userId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return linkId == that.linkId && userId == that.userId;
        }

        @Override
        public String toString() {
            return "MediaLinkOverride.CompositeId{" +
                    "linkId=" + linkId +
                    ", userId=" + userId +
                    '}';
        }
    }

}
