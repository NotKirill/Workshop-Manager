package notkirill.workshopmanager.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login/username")
    public AuthResponse loginByLocalLogin(@RequestBody @Valid AuthRequest request,
                                          HttpServletRequest servletRequest) {
        servletRequest.setAttribute("auth_source", AuthSource.LOCAL_USERNAME);
        return authService.authenticate(request);
    }

    @PostMapping("login/email")
    public AuthResponse loginByEmail(@RequestBody @Valid AuthRequest request,
                                     HttpServletRequest servletRequest) {
        servletRequest.setAttribute("auth_source", AuthSource.LOCAL_EMAIL);
        return authService.authenticate(request);
    }

    @PostMapping("login/steam")
    public AuthResponse loginBySteam(@RequestBody @Valid AuthRequest request,
                                     HttpServletRequest servletRequest) {
        // Long TODO finish
        return null;
    }

    @PostMapping("refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request,
                                HttpServletRequest servletRequest) {
        return authService.refresh(request);
    }

    @PostMapping("logout")
    public void logout(@RequestBody LogoutRequest request,
                       HttpServletRequest servletRequest) {
        authService.logout(request);
    }
}
