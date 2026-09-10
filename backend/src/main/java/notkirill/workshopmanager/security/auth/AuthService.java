package notkirill.workshopmanager.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import notkirill.workshopmanager.exception.UnauthorizedException;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.security.UserDetailsServiceImpl;
import notkirill.workshopmanager.security.auth.jwt.JwtService;
import notkirill.workshopmanager.security.auth.refresh.ClientInfoService;
import notkirill.workshopmanager.security.auth.refresh.NewRefreshTokenDto;
import notkirill.workshopmanager.security.auth.refresh.RefreshToken;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenService;
import notkirill.workshopmanager.security.auth.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {
    private final AuthNormalizer authNormalizer;
    private final AuthValidator authValidator;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ClientInfoService clientInfoService;
    private final OneTimeTokenService ottService;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthService(AuthNormalizer authNormalizer, AuthValidator authValidator, JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       ClientInfoService clientInfoService, OneTimeTokenService ottService,
                       AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.authNormalizer = authNormalizer;
        this.authValidator = authValidator;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.clientInfoService = clientInfoService;
        this.ottService = ottService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse authenticateByAuthRequest(AuthRequest request, HttpServletRequest servletRequest) {
        UserDetails user = authenticate(request);
        return approveAuthentication(servletRequest, user);
    }

    public AuthResponse authenticateBySignInToken(String ott, HttpServletRequest servletRequest) {
        ApprovedAuthPayload payload = ottService.consumeToken(ott, ApprovedAuthPayload.class);
        return resolveBySource(payload.provider(), payload.providerKey(), servletRequest);
    }

    private AuthResponse resolveBySource(AuthSource provider, String providerKey, HttpServletRequest servletRequest) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadByIdentity(provider, providerKey);
        } catch (UsernameNotFoundException _) {
            throw new UnauthorizedException(
                    "ACCOUNT_NOT_LINKED",
                    "This " + provider + " account is not linked to any user. Ask an administrator for access."
            );
        }
        requireActive(userDetails);
        return approveAuthentication(servletRequest, userDetails);
    }

    public AuthResponse refresh(RefreshRequest request, HttpServletRequest servletRequest) {
        RefreshToken token = refreshTokenService.getByToken(request.refreshToken());
        refreshTokenService.validateToken(token);

        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(String.valueOf(token.getUserId()));
        } catch (UsernameNotFoundException e) {
            throw new UnauthorizedException("REFRESH_TOKEN_USER_GONE", "Account no longer exists");
        }
        requireActive(user);

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

    private UserDetails authenticate(AuthRequest request) {
        AuthRequest normalized = preProcessAuthRequest(request);
        Authentication resolvedToken = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalized.login(), normalized.password()));
        return (UserDetails) Objects.requireNonNull(resolvedToken.getPrincipal());
    }

    private AuthResponse approveAuthentication(HttpServletRequest servletRequest, UserDetails user) {
        String jwt = jwtService.generateToken(user);
        NewRefreshTokenDto tokenData = refreshTokenService.create(
                Long.valueOf(user.getUsername()),
                clientInfoService.getClientInfo(servletRequest)
        );
        return new AuthResponse(jwt, tokenData.token());
    }

    private void requireActive(UserDetails user) {
        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account is locked");
        }
    }

    private AuthRequest preProcessAuthRequest(AuthRequest request) {
        String login = request.login();
        String password = authNormalizer.normalizePassword(request.password());

        if (AuthSource.ofLogin(login) == AuthSource.LOCAL_EMAIL) {
            login = authNormalizer.normalizeEmail(login);
            authValidator.validateEmail(login);
        } else {
            login = authNormalizer.normalizeUsername(login);
            authValidator.validateUsername(login);
        }

        authValidator.validatePasswordSignIn(password);

        return new AuthRequest(login, password);
    }
}