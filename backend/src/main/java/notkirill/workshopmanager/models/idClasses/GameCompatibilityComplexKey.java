package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class GameCompatibilityComplexKey implements Serializable {
    private long sourceGameId;
    private long tagetGameId;

    protected GameCompatibilityComplexKey() {
        // Required by JPA
    }

    public GameCompatibilityComplexKey(long sourceGameId, long tagetGameId) {
        this.sourceGameId = sourceGameId;
        this.tagetGameId = tagetGameId;
    }

    public long getSourceGameId() {
        return sourceGameId;
    }

    public void setSourceGameId(long sourceGameId) {
        this.sourceGameId = sourceGameId;
    }

    public long getTagetGameId() {
        return tagetGameId;
    }

    public void setTagetGameId(long tagetGameId) {
        this.tagetGameId = tagetGameId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameCompatibilityComplexKey that)) return false;
        return sourceGameId == that.sourceGameId && tagetGameId == that.tagetGameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceGameId, tagetGameId);
    }

    @Override
    public String toString() {
        return "GameCompatibilityComplexKey{" +
                "sourceGameId=" + sourceGameId +
                ", tagetGameId=" + tagetGameId +
                '}';
    }
}

