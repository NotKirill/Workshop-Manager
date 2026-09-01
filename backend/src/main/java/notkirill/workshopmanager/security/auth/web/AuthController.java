package notkirill.workshopmanager.security.auth.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.security.auth.ApprovedAuthPayload;
import notkirill.workshopmanager.security.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final OneTimeTokenService ottService;

    @Autowired
    public AuthController(AuthService authService, OneTimeTokenService ottService) {
        this.authService = authService;
        this.ottService = ottService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest authRequest, HttpServletRequest servletRequest) {
        return authService.authenticateByAuthRequest(authRequest, servletRequest);
    }

    @PostMapping("/login-success")
    public AuthResponse loginSuccess(@RequestBody AuthSuccessRequest request, HttpServletRequest servletRequest) {
        ApprovedAuthPayload payload = ottService.consumeToken(request.ott(), ApprovedAuthPayload.class);
        return authService.resolveBySource(payload.provider(), payload.providerKey(), servletRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request, HttpServletRequest servletRequest) {
        return authService.refresh(request, servletRequest);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
    }
}
