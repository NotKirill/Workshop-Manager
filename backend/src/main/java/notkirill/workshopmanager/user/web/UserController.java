package notkirill.workshopmanager.user.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.security.auth.AuthSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/me")
public class UserController {
    private final UserWebService userWebService;

    @Autowired
    public UserController(UserWebService userWebService) {
        this.userWebService = userWebService;
    }

    @GetMapping
    public ProfileResponse getCurrent(@AuthenticationPrincipal UserPrincipal principal) {
        return userWebService.getCurrentUser(principal);
    }

    @PatchMapping
    public ProfileResponse rename(@Valid @RequestBody RenameProfileRequest request,
                                 @AuthenticationPrincipal UserPrincipal principal) {
        return userWebService.rename(principal, request.name());
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request,
                               @AuthenticationPrincipal UserPrincipal principal) {
        userWebService.changePassword(principal, request);
    }

    @PutMapping("/identities/{provider}")
    public ProfileResponse setIdentity(@PathVariable AuthSource provider,
                                      @Valid @RequestBody SetIdentityRequest request,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        return userWebService.setLocalIdentity(principal, provider, request.key());
    }

    @PostMapping("/identities/steam/link-url")
    public Map<String, String> createSteamLinkUrl(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of("url", userWebService.createSteamLinkUrl(principal));
    }

    @PostMapping("/identities/steam")
    public ProfileResponse linkSteam(@Valid @RequestBody LinkSteamRequest request,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        return userWebService.linkSteam(principal, request.ott());
    }

    @DeleteMapping("/identities/{provider}")
    public ProfileResponse removeIdentity(@PathVariable AuthSource provider,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return userWebService.removeIdentity(principal, provider);
    }
}
