package notkirill.workshopmanager.user.dto;

import notkirill.workshopmanager.user.User;
import notkirill.workshopmanager.user.permission.PermissionEnum;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

public record UserSecurityDto(
        Long id,
        String passwordHash,
        OffsetDateTime timeCreated,
        String name,
        PermissionEnum[] permissions,
        Boolean enabled,
        Boolean accountNonLocked
) {
    public static UserSecurityDto of(User user) {
        return new UserSecurityDto(
                user.getId(),
                user.getPasswordHash(),
                user.getTimeCreated(),
                user.getName(),
                user.getPermissions(),
                user.isEnabled(),
                user.isAccountNonLocked()
        );
    }

    @Override
    @SuppressWarnings({"java:S6878", "DeconstructionCanBeUsed"})
    public boolean equals(Object o) {
        if (!(o instanceof UserSecurityDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(
                name, that.name) && Objects.equals(enabled, that.enabled) && Objects.equals(
                passwordHash, that.passwordHash) && Objects.equals(
                accountNonLocked, that.accountNonLocked) && Objects.equals(
                timeCreated, that.timeCreated) && Objects.deepEquals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, passwordHash, timeCreated, name, Arrays.hashCode(permissions), enabled, accountNonLocked);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "UserSecurityDto{" +
                "id=" + id +
                ", passwordHash=" + (passwordHash == null ? "null" : "[hidden]") +
                ", timeCreated=" + timeCreated +
                ", name='" + name + '\'' +
                ", permissions=" + Arrays.toString(permissions) +
                ", enabled=" + enabled +
                ", accountNonLocked=" + accountNonLocked +
                '}';
    }
}
