package notkirill.workshopmanager.user.admin;

import jakarta.validation.Valid;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.web.PermissionInfoResponse;
import notkirill.workshopmanager.user.web.SetIdentityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('USERS_MANAGE')")
public class AdminUserController {
    private final AdminUserWebService adminUserWebService;

    @Autowired
    public AdminUserController(AdminUserWebService adminUserWebService) {
        this.adminUserWebService = adminUserWebService;
    }

    @GetMapping
    public List<AdminUserResponse> list() {
        return adminUserWebService.list();
    }

    @GetMapping("/permissions")
    public List<PermissionInfoResponse> permissions() {
        return adminUserWebService.permissions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminUserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return adminUserWebService.create(request);
    }

    @PatchMapping("/{id}")
    public AdminUserResponse update(@PathVariable long id, @Valid @RequestBody UpdateUserRequest request,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        return adminUserWebService.update(id, request, principal);
    }

    @PostMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@PathVariable long id, @Valid @RequestBody ResetPasswordRequest request) {
        adminUserWebService.resetPassword(id, request.password());
    }

    @PutMapping("/{id}/identities/{provider}")
    public AdminUserResponse setIdentity(@PathVariable long id, @PathVariable AuthSource provider,
                                         @Valid @RequestBody SetIdentityRequest request) {
        return adminUserWebService.setIdentity(id, provider, request.key());
    }

    @DeleteMapping("/{id}/identities/{provider}")
    public AdminUserResponse removeIdentity(@PathVariable long id, @PathVariable AuthSource provider) {
        return adminUserWebService.removeIdentity(id, provider);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        adminUserWebService.delete(id, principal);
    }
}
