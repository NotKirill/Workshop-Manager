package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.UserGroupItemComplexKey;

@Entity
@Table(name = "user_group_items")
@IdClass(UserGroupItemComplexKey.class)
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
}
