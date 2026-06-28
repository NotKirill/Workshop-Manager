package notkirill.workshopmanager.models.idClasses;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class UserSharedPermissionComplexKey implements Serializable {
    private long userProviderId;
    private long userConsumerId;
    private short permissionId;

    protected UserSharedPermissionComplexKey() {
        // Required by JPA
    }

    public UserSharedPermissionComplexKey(long userProviderId, long userConsumerId, short permissionId) {
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
    public boolean equals(Object o) {
        if (!(o instanceof UserSharedPermissionComplexKey that)) return false;
        return userProviderId == that.userProviderId && userConsumerId == that.userConsumerId && permissionId == that.permissionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProviderId, userConsumerId, permissionId);
    }

    @Override
    public String toString() {
        return "UserSharedPermissionComplexKey{" +
                "userProviderId=" + userProviderId +
                ", userConsumerId=" + userConsumerId +
                ", permissionId=" + permissionId +
                '}';
    }
}
