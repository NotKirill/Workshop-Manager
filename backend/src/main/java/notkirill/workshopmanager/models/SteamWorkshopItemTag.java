package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "steam_workshop_item_tags")
@IdClass(SteamWorkshopItemTag.CompositeId.class)
public class SteamWorkshopItemTag {
    @Id
    @NotNull
    @Column(name = "internal_id", nullable = false)
    private long internalId;

    @Id
    @NotNull
    @Column(name = "tag_id", nullable = false)
    private long tagId;

    protected SteamWorkshopItemTag() {
        // Required by JPA
    }

    public SteamWorkshopItemTag(long internalId, long tagId) {
        this.internalId = internalId;
        this.tagId = tagId;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "SteamWorkshopItemTag{" +
                "internalId=" + internalId +
                ", tagId=" + tagId +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long internalId;
        private long tagId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long internalId, long tagId) {
            this.internalId = internalId;
            this.tagId = tagId;
        }

        public long getInternalId() {
            return internalId;
        }

        public void setInternalId(long internalId) {
            this.internalId = internalId;
        }

        public long getTagId() {
            return tagId;
        }

        public void setTagId(long tagId) {
            this.tagId = tagId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(internalId, tagId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return internalId == that.internalId && tagId == that.tagId;
        }

        @Override
        public String toString() {
            return "SteamWorkshopItemTag.CompositeId{" +
                    "internalId=" + internalId +
                    ", tagId=" + tagId +
                    '}';
        }
    }

}
