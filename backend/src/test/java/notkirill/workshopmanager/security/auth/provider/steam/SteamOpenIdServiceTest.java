package notkirill.workshopmanager.security.auth.provider.steam;

import notkirill.workshopmanager.FrontendProperties;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.ExternalServiceException;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.ratelimit.ThirdPartyInterceptor;
import notkirill.workshopmanager.ratelimit.exception.RateLimitExceededException;
import notkirill.workshopmanager.security.auth.ApprovedAuthPayload;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.security.auth.openid.OpenIdNonceService;
import notkirill.workshopmanager.security.auth.openid.OpenIdProperties;
import notkirill.workshopmanager.security.auth.openid.OpenIdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SteamOpenIdServiceTest {
    private static final Instant NOW = Instant.parse("2025-01-15T15:15:00Z");
    private static final String STATE = "0123456789abcdef0123456789abcdef";
    private static final String RETURN_TO = "https://wm.example/api/auth/steam/login-callback?state=" + STATE;
    private static final String STEAM_ID = "76561197960287930";
    private static final String CLAIMED_ID = "https://steamcommunity.com/openid/id/" + STEAM_ID;

    private OpenIdNonceService nonceService;
    private OneTimeTokenService tokenService;
    private RestClient restClient;
    private RestClient.RequestBodyUriSpec steamRequest;
    private RestClient.ResponseSpec steamResponse;
    private SteamOpenIdService service;

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() {
        nonceService = mock(OpenIdNonceService.class);
        tokenService = mock(OneTimeTokenService.class);
        restClient = mock(RestClient.class);
        steamRequest = mock(RestClient.RequestBodyUriSpec.class, RETURNS_SELF);
        steamResponse = mock(RestClient.ResponseSpec.class);
        when(restClient.post()).thenReturn(steamRequest);
        when(steamRequest.retrieve()).thenReturn(steamResponse);
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        OpenIdProperties openIdProperties = new OpenIdProperties(null, Duration.ofMinutes(15));
        service = new SteamOpenIdService(
                nonceService, new OpenIdValidator(clock, openIdProperties),
                tokenService, new FrontendProperties(URI.create("https://wm.example")), openIdProperties, restClient
        );

        when(nonceService.existByNonce(anyString(), anyString())).thenReturn(false);
        when(nonceService.tryRegister(anyString(), anyString())).thenReturn(true);
        when(tokenService.generateToken(any())).thenReturn("ott-token");
        steamAnswers("ns:http://specs.openid.net/auth/2.0\nis_valid:true\n");
    }

    @Test
    void loginRedirectUsesConfiguredRealmAndReturnTo() {
        String url = service.constructLoginRedirect(STATE);

        MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        assertThat(decode(params.getFirst("openid.return_to"))).isEqualTo(RETURN_TO);
        assertThat(decode(params.getFirst("openid.realm"))).isEqualTo("https://wm.example");
    }

    @Test
    void rejectsMalformedState() {
        assertThatThrownBy(() -> service.constructLoginRedirect("short"))
                .isInstanceOfSatisfying(
                        BadRequestException.class,
                        e -> assertThat(e.getCode()).isEqualTo("INVALID_SIGN_IN_STATE")
                );
        assertThatThrownBy(() -> service.constructLoginRedirect("x&openid.mode=y-0123456789abcdef"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void acceptsValidAssertion() {
        String token = service.authenticateLogin(validCallback());

        assertThat(token).isEqualTo("ott-token");
        verify(tokenService).generateToken(new ApprovedAuthPayload(AuthSource.STEAM, STEAM_ID));
        verify(nonceService).tryRegister(eq("2025-01-15T15:15:00Zabc"), anyString());
    }

    @Test
    void rejectsAssertionMadeForAnotherSite() {
        MultiValueMap<String, String> request = validCallback();
        request.set("openid.return_to", "https://evil.example/api/auth/steam/login-callback?state=" + STATE);

        assertThatThrownBy(() -> service.authenticateLogin(request)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(tokenService);
    }

    @Test
    void rejectsClaimedIdOutsideOfSignature() {
        MultiValueMap<String, String> request = validCallback();
        request.set("openid.signed", "signed,op_endpoint,identity,return_to,response_nonce,assoc_handle");

        assertThatThrownBy(() -> service.authenticateLogin(request)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(tokenService);
    }

    @Test
    void rejectsClaimedIdDifferentFromIdentity() {
        MultiValueMap<String, String> request = validCallback();
        request.set("openid.claimed_id", "https://steamcommunity.com/openid/id/76561197960287931");

        assertThatThrownBy(() -> service.authenticateLogin(request)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(tokenService);
    }

    @Test
    void rejectsAssertionSteamDoesNotConfirm() {
        steamAnswers("ns:http://specs.openid.net/auth/2.0\nis_valid:false\n");

        assertThatThrownBy(() -> service.authenticateLogin(validCallback()))
                .isInstanceOf(IllegalArgumentException.class);
        verify(nonceService, never()).tryRegister(anyString(), anyString());
        verifyNoInteractions(tokenService);
    }

    @Test
    void rejectsReplayedNonce() {
        when(nonceService.tryRegister(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> service.authenticateLogin(validCallback()))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(tokenService);
    }

    @Test
    void checkWithSteamIsMarkedNoRetry() {
        when(steamResponse.body(String.class)).thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> service.authenticateLogin(validCallback()))
                .isInstanceOf(ExternalServiceException.class);
        verify(steamRequest).attribute(ThirdPartyInterceptor.NO_RETRY, true);
        verify(steamResponse, times(1)).body(String.class);
    }

    @Test
    void steamServerErrorIsTransientAndBurnsNoNonce() {
        when(steamResponse.body(String.class)).thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> service.authenticateLogin(validCallback()))
                .isInstanceOfSatisfying(
                        ExternalServiceException.class,
                        e -> assertThat(e.isTransientFailure()).isTrue()
                );
        verify(nonceService, never()).tryRegister(anyString(), anyString());
    }

    @Test
    void refusedCheckBurnsNoNonce() {
        when(steamResponse.body(String.class)).thenThrow(
                new RateLimitExceededException("THIRD_PARTY_RATE_LIMIT", "quota used up"));

        assertThatThrownBy(() -> service.authenticateLogin(validCallback()))
                .isInstanceOf(RateLimitExceededException.class);
        verify(nonceService, never()).tryRegister(anyString(), anyString());
        verifyNoInteractions(tokenService);
    }

    private MultiValueMap<String, String> validCallback() {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.set("state", STATE);
        request.set("openid.ns", "http://specs.openid.net/auth/2.0");
        request.set("openid.mode", "id_res");
        request.set("openid.op_endpoint", "https://steamcommunity.com/openid/login");
        request.set("openid.claimed_id", CLAIMED_ID);
        request.set("openid.identity", CLAIMED_ID);
        request.set("openid.return_to", RETURN_TO);
        request.set("openid.response_nonce", "2025-01-15T15:15:00Zabc");
        request.set("openid.assoc_handle", "1234567890");
        request.set(
                "openid.signed",
                "signed,op_endpoint,claimed_id,identity,return_to,response_nonce,assoc_handle"
        );
        request.set("openid.sig", "c2lnbmF0dXJl");
        return request;
    }

    private void steamAnswers(String body) {
        when(steamResponse.body(String.class)).thenReturn(body);
    }
}
