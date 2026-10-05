package notkirill.workshopmanager.game.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.common.Texts;

import java.time.OffsetDateTime;

@Entity
@Table(name = "game_metadata")
public class GameMetadata {
    public static final long UNKNOWN_ID = -1;

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "name_requested_at")
    private OffsetDateTime nameRequestedAt;

    protected GameMetadata() {
        // Required by JPA
    }

    public GameMetadata(String name) {
        setName(name);
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
        this.slug = Texts.toSlug(name);
    }

    public String getSlug() {
        return slug;
    }

    public OffsetDateTime getNameRequestedAt() {
        return nameRequestedAt;
    }

    public void setNameRequestedAt(OffsetDateTime nameRequestedAt) {
        this.nameRequestedAt = nameRequestedAt;
    }

    @Override
    public String toString() {
        return "GameMetadata{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", nameRequestedAt=" + nameRequestedAt +
                '}';
    }
}
