package notkirill.workshopmanager.security.auth.provider.steam;

import notkirill.workshopmanager.FrontendProperties;
import notkirill.workshopmanager.exception.ExternalServiceException;
import notkirill.workshopmanager.exception.ServiceUnavailableException;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Objects;

@Service
public class SteamWebService {
    private static final Logger log = LoggerFactory.getLogger(SteamWebService.class);

    private final SteamOpenIdService steamOpenIdService;
    private final FrontendProperties frontendProperties;

    @Autowired
    public SteamWebService(SteamOpenIdService steamOpenIdService, FrontendProperties frontendProperties) {
        this.steamOpenIdService = steamOpenIdService;
        this.frontendProperties = frontendProperties;
    }

    private static String errorCode(RuntimeException e) {
        boolean unavailable = e instanceof RateLimitExceededException || e instanceof ServiceUnavailableException
                || e instanceof ExternalServiceException external && external.isTransientFailure();
        return unavailable ? "steam_unavailable" : "steam_failed";
    }

    private static boolean isCancelled(MultiValueMap<String, String> params) {
        return "cancel".equals(params.getFirst("openid.mode"));
    }

    public String loginUrl(String state) {
        return steamOpenIdService.constructLoginRedirect(state);
    }

    public String finishLogin(MultiValueMap<String, String> params) {
        if (isCancelled(params)) {
            return frontendUrl("/login", Map.of("error", "steam_cancelled"));
        }
        try {
            String token = steamOpenIdService.authenticateLogin(params);
            String state = Objects.requireNonNull(params.getFirst("state"));
            return frontendUrl("/login-success", Map.of("ott", token, "state", state));
        } catch (RuntimeException e) {
            log.warn("Steam OpenID sign-in failed: {}", e.getMessage());
            return frontendUrl("/login", Map.of("error", errorCode(e)));
        }
    }

    private String frontendUrl(String path, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(frontendProperties.url()).path(path);
        params.forEach(builder::queryParam);
        return builder.build().encode().toUriString();
    }
}
