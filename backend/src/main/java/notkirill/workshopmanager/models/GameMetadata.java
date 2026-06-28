package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "game_metadata")
public class GameMetadata {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    protected GameMetadata() {
        // Required by JPA
    }

    public GameMetadata(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GameMetadata{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
