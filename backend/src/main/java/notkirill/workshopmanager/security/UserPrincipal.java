package notkirill.workshopmanager.security;

import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final String passwordHash;
    private final boolean accountNonLocked;
    private final boolean enabled;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UserSecurityDto userDto) {
        this.userId = userDto.id();
        this.passwordHash = userDto.passwordHash();
        this.accountNonLocked = userDto.accountNonLocked();
        this.enabled = userDto.enabled();

        this.authorities = Arrays.stream(userDto.permissions()).map(
                perm -> new SimpleGrantedAuthority(perm.name())).collect(Collectors.toSet());
    }

    public Long getUserId() {
        return userId;
    }

    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @NullMarked
    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
