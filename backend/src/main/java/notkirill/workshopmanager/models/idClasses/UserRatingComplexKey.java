package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class UserRatingComplexKey implements Serializable {
    private long userId;
    private long itemPersonalMetadataId;

    protected UserRatingComplexKey() {
        // Required by JPA
    }

    public UserRatingComplexKey(long userId, long itemPersonalMetadataId) {
        this.userId = userId;
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemPersonalMetadataId() {
        return itemPersonalMetadataId;
    }

    public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserRatingComplexKey that)) return false;
        return userId == that.userId && itemPersonalMetadataId == that.itemPersonalMetadataId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemPersonalMetadataId);
    }

    @Override
    public String toString() {
        return "UserRatingComplexKey{" +
                "userId=" + userId +
                ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                '}';
    }
}
