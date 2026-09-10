package notkirill.workshopmanager.user.permission;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_shared_permissions")
@IdClass(UserSharedPermission.CompositeId.class)
public class UserSharedPermission {
    @Id
    @NotNull
    @Column(name = "user_provider_id", nullable = false)
    private long userProviderId;

    @Id
    @NotNull
    @Column(name = "user_consumer_id", nullable = false)
    private long userConsumerId;

    @Id
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "permission", columnDefinition = "user_permission", nullable = false)
    private PermissionEnum permission;

    protected UserSharedPermission() {
        // Required by JPA
    }

    public UserSharedPermission(long userProviderId, long userConsumerId, PermissionEnum permission) {
        this.userProviderId = userProviderId;
        this.userConsumerId = userConsumerId;
        this.permission = permission;
    }

    public long getUserProviderId() {
        return userProviderId;
    }

    public void setUserProviderId(long userProviderId) {
        this.userProviderId = userProviderId;
    }

    public long getUserConsumerId() {
        return userConsumerId;
    }

    public void setUserConsumerId(long userConsumerId) {
        this.userConsumerId = userConsumerId;
    }

    public PermissionEnum getPermission() {
        return permission;
    }

    public void setPermission(PermissionEnum permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "UserSharedPermission{" +
                "userProviderId=" + userProviderId +
                ", userConsumerId=" + userConsumerId +
                ", permission=" + permission +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long userProviderId;
        private long userConsumerId;
        private PermissionEnum permission;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long userProviderId, long userConsumerId, PermissionEnum permission) {
            this.userProviderId = userProviderId;
            this.userConsumerId = userConsumerId;
            this.permission = permission;
        }

        public long getUserProviderId() {
            return userProviderId;
        }

        public void setUserProviderId(long userProviderId) {
            this.userProviderId = userProviderId;
        }

        public long getUserConsumerId() {
            return userConsumerId;
        }

        public void setUserConsumerId(long userConsumerId) {
            this.userConsumerId = userConsumerId;
        }

        public PermissionEnum getPermission() {
            return permission;
        }

        public void setPermission(PermissionEnum permission) {
            this.permission = permission;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userProviderId, userConsumerId, permission);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return userProviderId == that.userProviderId && userConsumerId == that.userConsumerId && permission == that.permission;
        }

        @Override
        public String toString() {
            return "UserSharedPermission.CompositeId{" +
                    "userProviderId=" + userProviderId +
                    ", userConsumerId=" + userConsumerId +
                    ", permission=" + permission +
                    '}';
        }
    }
}
