package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Arrays;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_user_login", columnNames = {"login"})
        })
public class User {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull
    @Column(name = "time_created", nullable = false)
    private OffsetDateTime timeCreated = OffsetDateTime.now();

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    // TODO rename
    @Column(name = "permissions_ids", columnDefinition = "SMALLINT[]")
    private short[] permissionsIds;

    protected User() {
        // Required by JPA
    }

    public User(String passwordHash, OffsetDateTime timeCreated, String name, short[] permissionsIds) {
        this.passwordHash = passwordHash;
        this.timeCreated = timeCreated;
        this.name = name;
        this.permissionsIds = permissionsIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(OffsetDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short[] getPermissionsIds() {
        return permissionsIds;
    }

    public void setPermissionsIds(short[] permissionsIds) {
        this.permissionsIds = permissionsIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", passwordHash='" + passwordHash + '\'' +
                ", timeCreated=" + timeCreated +
                ", name='" + name + '\'' +
                ", permissionsIds=" + Arrays.toString(permissionsIds) +
                '}';
    }
}
