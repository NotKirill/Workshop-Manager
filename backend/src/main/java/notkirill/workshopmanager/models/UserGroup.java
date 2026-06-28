package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_groups",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_group_identity", columnNames = {"user_id", "game_id"})
})
public class UserGroup {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private long gameId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    protected UserGroup() {
        // Required by JPA
    }

    public UserGroup(long userId, long gameId, String name) {
        this.userId = userId;
        this.gameId = gameId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", userId=" + userId +
                ", gameId=" + gameId +
                ", name='" + name + '\'' +
                '}';
    }
}
