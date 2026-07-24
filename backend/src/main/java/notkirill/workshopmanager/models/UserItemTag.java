package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_item_tags")
@IdClass(UserItemTag.CompositeId.class)
public class UserItemTag {
    @Id
    @NotNull
    @Column(name = "tag_id", nullable = false)
    private long tagId;

    @Id
    @NotNull
    @Column(name = "item_personal_metadata_id", nullable = false)
    private long itemPersonalMetadataId;

    protected UserItemTag() {
        // Required by JPA
    }

    public UserItemTag(long tagId, long itemPersonalMetadataId) {
        this.tagId = tagId;
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getItemPersonalMetadataId() {
        return itemPersonalMetadataId;
    }

    public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    @Override
    public String toString() {
        return "UserItemTag{" +
                "tagId=" + tagId +
                ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long tagId;
        private long itemPersonalMetadataId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long tagId, long itemPersonalMetadataId) {
            this.tagId = tagId;
            this.itemPersonalMetadataId = itemPersonalMetadataId;
        }

        public long getTagId() {
            return tagId;
        }

        public void setTagId(long tagId) {
            this.tagId = tagId;
        }

        public long getItemPersonalMetadataId() {
            return itemPersonalMetadataId;
        }

        public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
            this.itemPersonalMetadataId = itemPersonalMetadataId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tagId, itemPersonalMetadataId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return tagId == that.tagId && itemPersonalMetadataId == that.itemPersonalMetadataId;
        }

        @Override
        public String toString() {
            return "UserItemTag.CompositeId{" +
                    "tagId=" + tagId +
                    ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                    '}';
        }
    }
}
