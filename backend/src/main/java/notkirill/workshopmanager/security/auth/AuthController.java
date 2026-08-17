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

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/login/steam")
    public AuthResponse loginBySteam(@RequestBody @Valid AuthRequest request,
                                     HttpServletRequest servletRequest) {
        // Long TODO finish
        return null;
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request,
                                HttpServletRequest servletRequest) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request,
                       HttpServletRequest servletRequest) {
        authService.logout(request);
    }
}
