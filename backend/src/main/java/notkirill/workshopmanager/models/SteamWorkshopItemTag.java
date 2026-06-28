package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.SteamWorkshopItemTagComplexKey;

@Entity
@Table(name = "steam_workshop_item_tags")
@IdClass(SteamWorkshopItemTagComplexKey.class)
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
}
