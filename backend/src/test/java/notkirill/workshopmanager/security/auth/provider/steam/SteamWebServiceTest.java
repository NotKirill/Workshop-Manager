package notkirill.workshopmanager.security.auth.provider.steam;

import notkirill.workshopmanager.FrontendProperties;
import notkirill.workshopmanager.exception.ExternalServiceException;
import notkirill.workshopmanager.exception.ServiceUnavailableException;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SteamWebServiceTest {
    private SteamOpenIdService service;
    private SteamWebService webService;

    @BeforeEach
    void setUp() {
        service = mock(SteamOpenIdService.class);
        webService = new SteamWebService(service, new FrontendProperties(URI.create("https://wm.example")));
    }

    @Test
    void refusedOrTransientFailuresAskToTryAgainLater() {
        for (RuntimeException e : new RuntimeException[]{
                new RateLimitExceededException("THIRD_PARTY_RATE_LIMIT", "quota used up"),
                new ServiceUnavailableException("THIRD_PARTY_UNAVAILABLE", "circuit open"),
                new ExternalServiceException("STEAM_OPENID_ERROR", "HTTP 503", true)}) {
            doThrow(e).when(service).authenticateLogin(any());

            assertThat(loginRedirect()).isEqualTo("https://wm.example/login?error=steam_unavailable");
        }
    }

    @Test
    void rejectedAssertionFails() {
        when(service.authenticateLogin(any())).thenThrow(
                new IllegalArgumentException("Steam rejected OpenID assertion"));

        assertThat(loginRedirect()).isEqualTo("https://wm.example/login?error=steam_failed");
    }

    @Test
    void permanentSteamErrorFails() {
        when(service.authenticateLogin(any()))
                .thenThrow(new ExternalServiceException("STEAM_OPENID_ERROR", "HTTP 400", false));

        assertThat(loginRedirect()).isEqualTo("https://wm.example/login?error=steam_failed");
    }

    private String loginRedirect() {
        return webService.finishLogin(new LinkedMultiValueMap<>());
    }
}
