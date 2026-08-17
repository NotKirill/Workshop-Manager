package notkirill.workshopmanager.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import notkirill.workshopmanager.SpringUtils;
import notkirill.workshopmanager.security.UserDetailsServiceImpl;
import notkirill.workshopmanager.security.auth.jwt.JwtService;
import notkirill.workshopmanager.security.auth.refresh.NewRefreshTokenDto;
import notkirill.workshopmanager.security.auth.refresh.RefreshToken;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenService;
import notkirill.workshopmanager.user.ClientInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {
    private final AuthNormalizer authNormalizer;
    private final AuthValidator authValidator;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ClientInfoService clientInfoService;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthService(AuthNormalizer authNormalizer, AuthValidator authValidator, JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       ClientInfoService clientInfoService,
                       AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.authNormalizer = authNormalizer;
        this.authValidator = authValidator;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.clientInfoService = clientInfoService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse authenticate(AuthRequest request) {
        HttpServletRequest servletRequest = SpringUtils.extractRequest();
        AuthRequest normalized = preProcessAuthRequest(request, servletRequest);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                normalized.login(),
                normalized.password()
        );
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        UserDetails user = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(Objects.requireNonNull(user));
        NewRefreshTokenDto tokenData = refreshTokenService.create(
                Long.valueOf(user.getUsername()),
                clientInfoService.getClientInfo(servletRequest)
        );
        return new AuthResponse(jwt, tokenData.token());
    }

    public AuthResponse refresh(RefreshRequest request) {
        HttpServletRequest servletRequest = SpringUtils.extractRequest();

        RefreshToken token = refreshTokenService.getByToken(request.refreshToken());
        refreshTokenService.validateToken(token);

        UserDetails user = userDetailsService.loadUserByUsername(String.valueOf(token.getUserId()));

        String jwt = jwtService.generateToken(Objects.requireNonNull(user));
        NewRefreshTokenDto newTokenData = refreshTokenService.rotateToken(
                token,
                clientInfoService.getClientInfo(servletRequest)
        );
        return new AuthResponse(jwt, newTokenData.token());
    }

    public void logout(LogoutRequest request) {
        RefreshToken token = refreshTokenService.getByToken(request.refreshToken());
        refreshTokenService.validateToken(token);

        refreshTokenService.revokeAllByFamilyId(token.getFamilyId());
    }

    private AuthRequest preProcessAuthRequest(AuthRequest request, HttpServletRequest servletRequest) {
        String login = request.login();
        String password = authNormalizer.normalizePassword(request.password());

        if (login.contains("@")) {
            login = authNormalizer.normalizeEmail(login);
            authValidator.validateEmail(login);
            servletRequest.setAttribute("auth_source", AuthSource.LOCAL_EMAIL);
        } else {
            login = authNormalizer.normalizeUsername(login);
            authValidator.validateUsername(login);
            servletRequest.setAttribute("auth_source", AuthSource.LOCAL_USERNAME);
        }

        authValidator.validatePasswordSignIn(password);

        return new AuthRequest(login, password);
    }
}