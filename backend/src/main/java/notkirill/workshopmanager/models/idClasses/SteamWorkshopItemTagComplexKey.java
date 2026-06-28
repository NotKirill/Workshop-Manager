package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class SteamWorkshopItemTagComplexKey implements Serializable {
    private long internalId;
    private long tagId;

    protected SteamWorkshopItemTagComplexKey() {
        // Required by JPA
    }

    public SteamWorkshopItemTagComplexKey(long internalId, long tagId) {
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
    public boolean equals(Object o) {
        if (!(o instanceof SteamWorkshopItemTagComplexKey that)) return false;
        return internalId == that.internalId && tagId == that.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId, tagId);
    }

    @Override
    public String toString() {
        return "SteamWorkshopItemTagComplexKey{" +
                "internalId=" + internalId +
                ", tagId=" + tagId +
                '}';
    }
}
