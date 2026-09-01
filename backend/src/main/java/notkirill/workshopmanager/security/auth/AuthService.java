package notkirill.workshopmanager.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import notkirill.workshopmanager.security.UserDetailsServiceImpl;
import notkirill.workshopmanager.security.auth.jwt.JwtService;
import notkirill.workshopmanager.security.auth.refresh.NewRefreshTokenDto;
import notkirill.workshopmanager.security.auth.refresh.RefreshToken;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenService;
import notkirill.workshopmanager.security.auth.web.AuthRequest;
import notkirill.workshopmanager.security.auth.web.AuthResponse;
import notkirill.workshopmanager.security.auth.web.LogoutRequest;
import notkirill.workshopmanager.security.auth.web.RefreshRequest;
import notkirill.workshopmanager.user.ClientInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public AuthResponse authenticateByAuthRequest(AuthRequest request, HttpServletRequest servletRequest) {
        AuthRequest normalized = preProcessAuthRequest(request, servletRequest);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                normalized.login(),
                normalized.password()
        );
        Authentication resolvedToken = authenticationManager.authenticate(token);

        UserDetails user = (UserDetails) resolvedToken.getPrincipal();
        return approveAuthentication(servletRequest, Objects.requireNonNull(user));
    }

    public AuthResponse resolveBySource(AuthSource provider, String providerKey, HttpServletRequest servletRequest) {
        servletRequest.setAttribute("auth_source", provider);

        UserDetails userDetails = userDetailsService.loadUserByUsername(providerKey);
        return approveAuthentication(servletRequest, userDetails);
    }

    public AuthResponse refresh(RefreshRequest request, HttpServletRequest servletRequest) {
        RefreshToken token = refreshTokenService.getByToken(request.refreshToken());
        refreshTokenService.validateToken(token);

        UserDetails user = userDetailsService.loadUserByUsername(String.valueOf(token.getUserId()));

        String jwt = jwtService.generateToken(user);
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

    private AuthResponse approveAuthentication(HttpServletRequest servletRequest, UserDetails user) {
        String jwt = jwtService.generateToken(user);
        NewRefreshTokenDto tokenData = refreshTokenService.create(
                Long.valueOf(user.getUsername()),
                clientInfoService.getClientInfo(servletRequest)
        );
        return new AuthResponse(jwt, tokenData.token());
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