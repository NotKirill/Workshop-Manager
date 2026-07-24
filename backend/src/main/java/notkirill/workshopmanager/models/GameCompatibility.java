package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "game_compatibility")
@IdClass(GameCompatibility.CompositeId.class)
public class GameCompatibility {
    @Id
    @NotNull
    @Column(name = "source_game_id", nullable = false)
    private long sourceGameId;

    @Id
    @NotNull
    @Column(name = "target_game_id", nullable = false)
    private long targetGameId;

    protected GameCompatibility() {
        // Required by JPA
    }

    public GameCompatibility(long sourceGameId, long targetGameId) {
        this.sourceGameId = sourceGameId;
        this.targetGameId = targetGameId;
    }

    public long getSourceGameId() {
        return sourceGameId;
    }

    public void setSourceGameId(long sourceGameId) {
        this.sourceGameId = sourceGameId;
    }

    public long getTargetGameId() {
        return targetGameId;
    }

    public void setTargetGameId(long targetGameId) {
        this.targetGameId = targetGameId;
    }

    @Override
    public String toString() {
        return "GameCompatibility{" +
                "sourceGameId=" + sourceGameId +
                ", targetGameId=" + targetGameId +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long sourceGameId;
        private long targetGameId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long sourceGameId, long targetGameId) {
            this.sourceGameId = sourceGameId;
            this.targetGameId = targetGameId;
        }

        public long getSourceGameId() {
            return sourceGameId;
        }

        public void setSourceGameId(long sourceGameId) {
            this.sourceGameId = sourceGameId;
        }

        public long getTargetGameId() {
            return targetGameId;
        }

        public void setTargetGameId(long targetGameId) {
            this.targetGameId = targetGameId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceGameId, targetGameId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return sourceGameId == that.sourceGameId && targetGameId == that.targetGameId;
        }

        @Override
        public String toString() {
            return "GameCompatibility.CompositeId{" +
                    "sourceGameId=" + sourceGameId +
                    ", targetGameId=" + targetGameId +
                    '}';
        }
    }
}
