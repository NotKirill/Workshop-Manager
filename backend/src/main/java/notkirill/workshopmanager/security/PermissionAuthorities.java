package notkirill.workshopmanager.security;

import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PermissionAuthorities {
    private PermissionAuthorities() {
    }

    public static boolean has(UserDetails user, PermissionEnum permission) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(permission.name()::equals);
    }

    public static List<PermissionEnum> of(UserDetails user) {
        Set<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return Arrays.stream(PermissionEnum.values())
                .filter(permission -> authorities.contains(permission.name()))
                .toList();
    }
}
