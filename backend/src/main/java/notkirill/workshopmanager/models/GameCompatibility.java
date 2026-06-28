package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.GameCompatibilityComplexKey;

@Entity
@Table(name = "game_compatibility")
@IdClass(GameCompatibilityComplexKey.class)
public class GameCompatibility {
    @Id
    @NotNull
    @Column(name = "source_game_id", nullable = false)
    private long sourceGameId;

    @Id
    @NotNull
    @Column(name = "target_game_id", nullable = false)
    private long tagetGameId;

    protected GameCompatibility() {
        // Required by JPA
    }

    public GameCompatibility(long sourceGameId, long tagetGameId) {
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
    public String toString() {
        return "GameCompatibility{" +
                "sourceGameId=" + sourceGameId +
                ", tagetGameId=" + tagetGameId +
                '}';
    }
}
