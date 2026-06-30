package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.ModspaceType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "game_to_modspace_ids",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_game_to_modspace_id_identity", columnNames = {"source_modspace", "external_id"})
        })
public class GameToModspaceId {
    @Id
    @NotNull
    @Column(name = "game_id", nullable = false)
    private long gameId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "source_modspace", columnDefinition = "modspace_type", nullable = false)
    private ModspaceType sourceModspace;

    @NotNull
    @Column(name = "external_id", nullable = false)
    private String externalId;

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
}
