package notkirill.workshopmanager.security.auth.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import notkirill.workshopmanager.security.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest authRequest, HttpServletRequest servletRequest) {
        return authService.authenticateByAuthRequest(authRequest, servletRequest);
    }

    @PostMapping("/login-success")
    public AuthResponse loginSuccess(@RequestBody AuthSuccessRequest request, HttpServletRequest servletRequest) {
        return authService.authenticateBySignInToken(request.ott(), servletRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request, HttpServletRequest servletRequest) {
        return authService.refresh(request, servletRequest);
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody @Valid PasswordChangeRequest request) {
        authService.changePasswordBySignIn(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
    }
}
