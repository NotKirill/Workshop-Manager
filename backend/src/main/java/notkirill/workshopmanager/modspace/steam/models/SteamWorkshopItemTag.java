package notkirill.workshopmanager.modspace.steam.models;

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
    @Column(name = "local_id", nullable = false)
    private long localId;

    @Id
    @NotNull
    @Column(name = "tag_id", nullable = false)
    private long tagId;

    protected SteamWorkshopItemTag() {
        // Required by JPA
    }

    public SteamWorkshopItemTag(long localId, long tagId) {
        this.localId = localId;
        this.tagId = tagId;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
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
                "localId=" + localId +
                ", tagId=" + tagId +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long localId;
        private long tagId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long localId, long tagId) {
            this.localId = localId;
            this.tagId = tagId;
        }

        public long getLocalId() {
            return localId;
        }

        public void setLocalId(long localId) {
            this.localId = localId;
        }

        public long getTagId() {
            return tagId;
        }

        public void setTagId(long tagId) {
            this.tagId = tagId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(localId, tagId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return localId == that.localId && tagId == that.tagId;
        }

        @Override
        public String toString() {
            return "SteamWorkshopItemTag.CompositeId{" +
                    "localId=" + localId +
                    ", tagId=" + tagId +
                    '}';
        }
    }

}
