package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class UserItemTagComplexKey implements Serializable {
    private long tagId;
    private long itemPersonalMetadataId;

    protected UserItemTagComplexKey() {
        // Required by JPA
    }

    public UserItemTagComplexKey(long tagId, long itemPersonalMetadataId) {
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
    public boolean equals(Object o) {
        if (!(o instanceof UserItemTagComplexKey that)) return false;
        return tagId == that.tagId && itemPersonalMetadataId == that.itemPersonalMetadataId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, itemPersonalMetadataId);
    }

    @Override
    public String toString() {
        return "UserItemTagComplexKey{" +
                "tagId=" + tagId +
                ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                '}';
    }
}
