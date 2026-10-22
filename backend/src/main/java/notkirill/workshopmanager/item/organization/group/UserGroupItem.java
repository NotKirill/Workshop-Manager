package notkirill.workshopmanager.item.organization.group;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_group_items")
@IdClass(UserGroupItem.CompositeId.class)
public class UserGroupItem {
    @Id
    @NotNull
    @Column(name = "user_group_id", nullable = false)
    private long userGroupId;

    @Id
    @NotNull
    @Column(name = "item_personal_metadata_id", nullable = false)
    private long itemPersonalMetadataId;

    protected UserGroupItem() {
        // Required by JPA
    }

    public UserGroupItem(long userGroupId, long itemPersonalMetadataId) {
        this.userGroupId = userGroupId;
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    public long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(long userGroupId) {
        this.userGroupId = userGroupId;
    }

    public long getItemPersonalMetadataId() {
        return itemPersonalMetadataId;
    }

    public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    @Override
    public String toString() {
        return "UserGroupItem{" +
                "userGroupId=" + userGroupId +
                ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long userGroupId;
        private long itemPersonalMetadataId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long userGroupId, long itemPersonalMetadataId) {
            this.userGroupId = userGroupId;
            this.itemPersonalMetadataId = itemPersonalMetadataId;
        }

        public long getUserGroupId() {
            return userGroupId;
        }

        public void setUserGroupId(long userGroupId) {
            this.userGroupId = userGroupId;
        }

        public long getItemPersonalMetadataId() {
            return itemPersonalMetadataId;
        }

        public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
            this.itemPersonalMetadataId = itemPersonalMetadataId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userGroupId, itemPersonalMetadataId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return userGroupId == that.userGroupId && itemPersonalMetadataId == that.itemPersonalMetadataId;
        }

        @Override
        public String toString() {
            return "UserGroupItem.CompositeId{" +
                    "userGroupId=" + userGroupId +
                    ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                    '}';
        }
    }
}
