package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.UserItemTagComplexKey;

@Entity
@Table(name = "user_item_tags")
@IdClass(UserItemTagComplexKey.class)
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
}
