package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class MediaLinkOverrideComplexKey implements Serializable {
    private long linkId;
    private long userId;

    protected MediaLinkOverrideComplexKey() {
        // Required by JPA
    }

    public MediaLinkOverrideComplexKey(long linkId, long userId) {
        this.linkId = linkId;
        this.userId = userId;
    }

    public long getLinkId() {
        return linkId;
    }

    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MediaLinkOverrideComplexKey that)) return false;
        return linkId == that.linkId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkId, userId);
    }

    @Override
    public String toString() {
        return "MediaLinkOverrideComplexKey{" +
                "linkId=" + linkId +
                ", userId=" + userId +
                '}';
    }
}
