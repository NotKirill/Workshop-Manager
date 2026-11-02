package notkirill.workshopmanager.user.admin;

import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.*;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import notkirill.workshopmanager.user.web.IdentityResponse;
import notkirill.workshopmanager.user.web.PermissionInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserWebService {
    private final UserService userService;
    private final UserAccountService accountService;
    private final UserDeletionService deletionService;
    private final PersonalItemService personalItemService;

    @Autowired
    public AdminUserWebService(UserService userService, UserAccountService accountService,
                               UserDeletionService deletionService, PersonalItemService personalItemService) {
        this.userService = userService;
        this.accountService = accountService;
        this.deletionService = deletionService;
        this.personalItemService = personalItemService;
    }

    public List<AdminUserResponse> list() {
        List<User> users = userService.getAll();
        Map<Long, List<UserIdentity>> identities = userService.getIdentities(
                users.stream().map(User::getId).toList());
        return users.stream().map(user -> toResponse(user, identities.getOrDefault(user.getId(), List.of())))
                .toList();
    }

    public List<PermissionInfoResponse> permissions() {
        return Arrays.stream(PermissionEnum.values())
                .map(PermissionInfoResponse::of)
                .toList();
    }

    public AdminUserResponse create(CreateUserRequest request) {
        User user = accountService.create(
                request.name(), request.password(), request.identities(),
                request.permissions()
        );
        return toResponse(user);
    }

    public AdminUserResponse update(long id, UpdateUserRequest request, UserPrincipal principal) {
        User user = accountService.update(
                principal.getUserId(), id, request.name(), request.enabled(),
                request.accountNonLocked(), request.permissions()
        );
        return toResponse(user);
    }

    public void resetPassword(long id, String password) {
        accountService.resetPassword(id, password);
    }

    public AdminUserResponse setIdentity(long id, AuthSource provider, String key) {
        accountService.setIdentity(id, provider, key);
        return toResponse(userService.require(id));
    }

    public AdminUserResponse removeIdentity(long id, AuthSource provider) {
        accountService.removeIdentity(id, provider);
        return toResponse(userService.require(id));
    }

    public void delete(long id, UserPrincipal principal) {
        deletionService.delete(principal.getUserId(), id);
    }

    private AdminUserResponse toResponse(User user) {
        return toResponse(user, userService.getIdentities(user.getId()));
    }

    private AdminUserResponse toResponse(User user, List<UserIdentity> identities) {
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getTimeCreated(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.getPasswordHash() != null,
                userService.getPermissions(user),
                identities.stream().map(i -> new IdentityResponse(i.getProvider(), i.getProviderKey())).toList(),
                personalItemService.countOfUser(user.getId())
        );
    }
}
