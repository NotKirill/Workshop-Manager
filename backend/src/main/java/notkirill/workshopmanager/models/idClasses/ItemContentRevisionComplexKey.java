package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class ItemContentRevisionComplexKey implements Serializable {
    private long itemContentId;
    private long revision;

    protected ItemContentRevisionComplexKey() {
        // Required by JPA
    }

    public ItemContentRevisionComplexKey(long itemContentId, long revision) {
        this.itemContentId = itemContentId;
        this.revision = revision;
    }

    public long getItemContentId() {
        return itemContentId;
    }

    public void setItemContentId(long itemContentId) {
        this.itemContentId = itemContentId;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemContentRevisionComplexKey that)) return false;
        return itemContentId == that.itemContentId && revision == that.revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemContentId, revision);
    }

    @Override
    public String toString() {
        return "ItemContentRevisionComplexKey{" +
                "itemContentId=" + itemContentId +
                ", revision=" + revision +
                '}';
    }
}