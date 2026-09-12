package notkirill.workshopmanager.user;

import notkirill.workshopmanager.common.Texts;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.ConflictException;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.exception.UnauthorizedException;
import notkirill.workshopmanager.security.auth.AuthNormalizer;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.security.auth.AuthValidator;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenService;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserAccountService {
    private static final Pattern STEAM_ID = Pattern.compile("^\\d{17}$");

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final UserSecurityCache securityCache;
    private final AuthNormalizer normalizer;
    private final AuthValidator validator;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserAccountService(UserService userService, RefreshTokenService refreshTokenService,
                              UserSecurityCache securityCache, AuthNormalizer normalizer, AuthValidator validator,
                              BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.securityCache = securityCache;
        this.normalizer = normalizer;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    private static PermissionEnum[] toArray(Collection<PermissionEnum> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new PermissionEnum[0];
        }
        return EnumSet.copyOf(permissions).toArray(PermissionEnum[]::new);
    }

    private static void requireNoSelfLockout(Boolean enabled, Boolean accountNonLocked,
                                             Collection<PermissionEnum> permissions) {
        if (Boolean.FALSE.equals(enabled) || Boolean.FALSE.equals(accountNonLocked)) {
            throw new BadRequestException("SELF_LOCKOUT", "You cannot disable your own account");
        }
        if (permissions != null && !permissions.contains(PermissionEnum.USERS_MANAGE)) {
            throw new BadRequestException("SELF_LOCKOUT", "You cannot remove your own user management permission");
        }
    }

    @Transactional
    public User create(String name, String password, Map<AuthSource, String> identities,
                       Collection<PermissionEnum> permissions) {
        if (identities.isEmpty()) {
            throw new BadRequestException("IDENTITY_REQUIRED", "At least one sign-in identity is required");
        }
        String passwordHash = null;
        if (Texts.blankToNull(password) != null) {
            passwordHash = encodePassword(password);
        } else if (identities.keySet().stream().anyMatch(AuthSource::isPasswordBased)) {
            throw new BadRequestException("PASSWORD_REQUIRED", "Password-based sign-in needs a password");
        }
        String displayName = Optional.ofNullable(Texts.blankToNull(name))
                .or(() -> Optional.ofNullable(Texts.blankToNull(identities.get(AuthSource.LOCAL_USERNAME))))
                .orElse("User");
        User user = userService.save(new User(passwordHash, displayName, toArray(permissions)));
        identities.forEach((provider, key) -> addIdentity(user.getId(), provider, key));
        return user;
    }

    @Transactional
    public User update(long actorUserId, long userId, String name, Boolean enabled, Boolean accountNonLocked,
                       Collection<PermissionEnum> permissions) {
        if (actorUserId == userId) {
            requireNoSelfLockout(enabled, accountNonLocked, permissions);
        }
        User user = userService.require(userId);
        if (Texts.blankToNull(name) != null) {
            user.setName(name.strip());
        }
        boolean revokeSessions = false;
        if (enabled != null && enabled != user.isEnabled()) {
            user.setEnabled(enabled);
            revokeSessions |= !enabled;
        }
        if (accountNonLocked != null && accountNonLocked != user.isAccountNonLocked()) {
            user.setAccountNonLocked(accountNonLocked);
            revokeSessions |= !accountNonLocked;
        }
        if (permissions != null) {
            user.setPermissions(toArray(permissions));
        }
        userService.save(user);
        if (revokeSessions) {
            refreshTokenService.revokeAllByUserId(userId);
        }
        securityCache.evict(userId);
        return user;
    }

    @Transactional
    public void rename(long userId, String name) {
        User user = userService.require(userId);
        user.setName(name.strip());
        userService.save(user);
    }

    @Transactional
    public void changeOwnPassword(long userId, String currentPassword, String newPassword,
                                  String currentRefreshToken) {
        User user = userService.require(userId);
        if (user.getPasswordHash() != null) {
            String normalized = normalizer.normalizePassword(currentPassword);
            if (!passwordEncoder.matches(normalized, user.getPasswordHash())) {
                throw new UnauthorizedException("INVALID_PASSWORD", "Current password is wrong");
            }
        }
        setPassword(user, newPassword);
        refreshTokenService.revokeOtherSessions(userId, currentRefreshToken);
    }

    @Transactional
    public void resetPassword(long userId, String newPassword) {
        setPassword(userService.require(userId), newPassword);
        refreshTokenService.revokeAllByUserId(userId);
    }

    private void setPassword(User user, String newPassword) {
        user.setPasswordHash(encodePassword(newPassword));
        userService.save(user);
        securityCache.evict(user.getId());
    }

    @Transactional
    public UserIdentity setIdentity(long userId, AuthSource provider, String rawKey) {
        userService.require(userId);
        UserIdentity saved = addIdentity(userId, provider, rawKey);
        securityCache.evict(userId);
        return saved;
    }

    private UserIdentity addIdentity(long userId, AuthSource provider, String rawKey) {
        String key = normalizeIdentityKey(provider, rawKey);
        userService.findIdentity(provider, key)
                .filter(other -> other.getUserId() != userId)
                .ifPresent(other -> {
                    throw new ConflictException(
                            "IDENTITY_TAKEN", "This " + provider.getLabel()
                            + " is already used by another account"
                    );
                });
        UserIdentity identity = userService.findIdentity(userId, provider)
                .orElseGet(() -> new UserIdentity(userId, provider, key));
        identity.setProviderKey(key);
        return userService.saveIdentity(identity);
    }

    @Transactional
    public void removeIdentity(long userId, AuthSource provider) {
        User user = userService.require(userId);
        List<UserIdentity> identities = userService.getIdentities(userId);
        UserIdentity identity = identities.stream().filter(i -> i.getProvider() == provider).findFirst()
                .orElseThrow(() -> new NotFoundException("IDENTITY_NOT_FOUND", "Identity not found"));
        long remainingUsable = identities.stream()
                .filter(i -> i != identity)
                .filter(i -> !i.getProvider().isPasswordBased() || user.getPasswordHash() != null)
                .count();
        if (remainingUsable == 0) {
            throw new ConflictException("LAST_IDENTITY", "The account would have no way to sign in");
        }
        userService.deleteIdentity(identity);
        securityCache.evict(userId);
    }

    private String encodePassword(String password) {
        String normalized = normalizer.normalizePassword(password);
        validator.validatePasswordSignUp(normalized);
        return passwordEncoder.encode(normalized);
    }

    private String normalizeIdentityKey(AuthSource provider, String rawKey) {
        if (rawKey == null || rawKey.isBlank()) {
            throw new BadRequestException("IDENTITY_KEY_REQUIRED", "Value is required");
        }
        return switch (provider) {
            case LOCAL_USERNAME -> {
                String username = normalizer.normalizeUsername(rawKey);
                validator.validateUsername(username);
                yield username;
            }
            case LOCAL_EMAIL -> {
                String email = normalizer.normalizeEmail(rawKey);
                validator.validateEmail(email);
                yield email;
            }
            case STEAM -> {
                String steamId = rawKey.strip();
                if (!STEAM_ID.matcher(steamId).matches()) {
                    throw new BadRequestException("INVALID_STEAM_ID", "Steam id must be a 17-digit SteamID64");
                }
                yield steamId;
            }
        };
    }
}
