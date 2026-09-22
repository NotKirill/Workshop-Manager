package notkirill.workshopmanager.user.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.security.UserPrincipal;
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

    @PostMapping("/identities/steam/link-url")
    public Map<String, String> createSteamLinkUrl(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of("url", userWebService.createSteamLinkUrl(principal));
    }

    @PostMapping("/identities/steam")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkSteam(@Valid @RequestBody LinkSteamRequest request,
                          @AuthenticationPrincipal UserPrincipal principal) {
        userWebService.linkSteam(principal, request.ott());
    }
}
