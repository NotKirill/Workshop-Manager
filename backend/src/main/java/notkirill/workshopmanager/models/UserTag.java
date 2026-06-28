package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_tags")
public class UserTag {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "display_name")
    private String displayName;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private long gameId;

    protected UserTag() {
        // Required by JPA
    }

    public UserTag(String displayName, long gameId) {
        this.displayName = displayName;
        this.gameId = gameId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return "UserTag{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", gameId=" + gameId +
                '}';
    }
}
