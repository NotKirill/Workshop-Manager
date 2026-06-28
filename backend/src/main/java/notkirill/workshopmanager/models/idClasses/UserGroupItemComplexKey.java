package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class UserGroupItemComplexKey implements Serializable {
    private long userGroupId;
    private long itemPersonalMetadataId;

    protected UserGroupItemComplexKey() {
        // Required by JPA
    }

    public UserGroupItemComplexKey(long userGroupId, long itemPersonalMetadataId) {
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
    public boolean equals(Object o) {
        if (!(o instanceof UserGroupItemComplexKey that)) return false;
        return userGroupId == that.userGroupId && itemPersonalMetadataId == that.itemPersonalMetadataId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userGroupId, itemPersonalMetadataId);
    }

    @Override
    public String toString() {
        return "UserGroupItemComplexKey{" +
                "userGroupId=" + userGroupId +
                ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                '}';
    }
}
