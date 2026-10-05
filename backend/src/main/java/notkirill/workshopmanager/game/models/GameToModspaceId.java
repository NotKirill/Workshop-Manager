package notkirill.workshopmanager.game.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "game_to_modspace_ids")
@IdClass(GameToModspaceId.CompositeId.class)
public class GameToModspaceId {
    @Id
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "source_modspace", columnDefinition = "modspace_type", nullable = false)
    private ModspaceType sourceModspace;

    @Id
    @NotNull
    @Column(name = "external_id", nullable = false)
    private String externalId;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private long gameId;

    protected GameToModspaceId() {
        // Required by JPA
    }

    public GameToModspaceId(long gameId, ModspaceType sourceModspace, String externalId) {
        this.gameId = gameId;
        this.sourceModspace = sourceModspace;
        this.externalId = externalId;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public ModspaceType getSourceModspace() {
        return sourceModspace;
    }

    public void setSourceModspace(ModspaceType sourceModspace) {
        this.sourceModspace = sourceModspace;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "GameToModspaceId{" +
                "gameId=" + gameId +
                ", sourceModspace=" + sourceModspace +
                ", externalId='" + externalId + '\'' +
                '}';
    }

    public static class CompositeId implements Serializable {
        private ModspaceType sourceModspace;
        private String externalId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(ModspaceType sourceModspace, String externalId) {
            this.sourceModspace = sourceModspace;
            this.externalId = externalId;
        }

        public ModspaceType getSourceModspace() {
            return sourceModspace;
        }

        public void setSourceModspace(ModspaceType sourceModspace) {
            this.sourceModspace = sourceModspace;
        }

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceModspace, externalId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return sourceModspace == that.sourceModspace && Objects.equals(externalId, that.externalId);
        }

        @Override
        public String toString() {
            return "GameToModspaceId.CompositeId{" +
                    "sourceModspace=" + sourceModspace +
                    ", externalId='" + externalId + '\'' +
                    '}';
        }
    }
}
