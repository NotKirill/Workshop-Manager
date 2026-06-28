package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.UserSharedPermissionComplexKey;

@Entity
@Table(name = "user_shared_permissions")
@IdClass(UserSharedPermissionComplexKey.class)
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
    @Column(name = "permission_id", nullable = false)
    private short permissionId;

    protected UserSharedPermission() {
        // Required by JPA
    }

    public UserSharedPermission(long userProviderId, long userConsumerId, short permissionId) {
        this.userProviderId = userProviderId;
        this.userConsumerId = userConsumerId;
        this.permissionId = permissionId;
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

    public short getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(short permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        return "UserSharedPermission{" +
                "userProviderId=" + userProviderId +
                ", userConsumerId=" + userConsumerId +
                ", permissionId=" + permissionId +
                '}';
    }
}
