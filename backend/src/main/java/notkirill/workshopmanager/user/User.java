package notkirill.workshopmanager.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Arrays;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
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
    @CreatedDate
    @Column(name = "time_created", nullable = false, updatable = false)
    private OffsetDateTime timeCreated;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "permission_ids", columnDefinition = "SMALLINT[]")
    private short[] permissionIds;

    // Long TODO maybe integrate?
    @NotNull
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    // Long TODO maybe integrate?
    @NotNull
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    protected User() {
        // Required by JPA
    }

    public User(String passwordHash, String name, short[] permissionIds) {
        this.passwordHash = passwordHash;
        this.name = name;
        this.permissionIds = permissionIds;
    }

    public long getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short[] getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(short[] permissionIds) {
        this.permissionIds = permissionIds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", passwordHash='" + passwordHash + '\'' +
                ", timeCreated=" + timeCreated +
                ", name='" + name + '\'' +
                ", permissionIds=" + Arrays.toString(permissionIds) +
                ", enabled=" + enabled +
                ", accountNonLocked=" + accountNonLocked +
                '}';
    }
}
