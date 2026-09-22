package notkirill.workshopmanager.security.auth.provider.steam;

import notkirill.workshopmanager.FrontendProperties;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.ExternalServiceException;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.ratelimit.ApiClientConfig;
import notkirill.workshopmanager.security.auth.ApprovedAuthPayload;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.security.auth.openid.OpenIdMode;
import notkirill.workshopmanager.security.auth.openid.OpenIdNonceService;
import notkirill.workshopmanager.security.auth.openid.OpenIdProperties;
import notkirill.workshopmanager.security.auth.openid.OpenIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://www.openid.net/specs/openid-authentication-2_0-12.html
// Maybe TODO we can abstract it to OpenID 2.0 workflow with a discovery feature, but we don't need that right now
@Service
public class SteamOpenIdService {
    public static final String BASE_PATH = "/api/auth/steam";
    public static final String LOGIN_CALLBACK = "/login-callback";
    public static final String LINK_CALLBACK = "/link-callback";

    private static final String OPENID_NAMESPACE = "http://specs.openid.net/auth/2.0";
    private static final String OPENID_IDENTIFIER_SELECT = OPENID_NAMESPACE + "/identifier_select";

    private static final String STEAM_OPENID_ENDPOINT = "https://steamcommunity.com/openid";
    private static final Pattern STEAM_CLAIMED_ID_PATTERN =
            Pattern.compile("^https://steamcommunity\\.com/openid/id/(\\d+)$");
    private static final Pattern LOGIN_STATE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{16,128}$");
    private static final Set<String> REQUIRED_SIGNED_FIELDS = Set.of(
            "op_endpoint", "claimed_id", "identity", "return_to", "response_nonce", "assoc_handle");

    private final OpenIdNonceService nonceService;
    private final OpenIdValidator openIdValidator;
    private final OneTimeTokenService tokenService;
    private final FrontendProperties frontendProperties;
    private final OpenIdProperties openIdProperties;
    private final RestClient restClient;

    @Autowired
    public SteamOpenIdService(OpenIdNonceService nonceService, OpenIdValidator openIdValidator,
                              OneTimeTokenService tokenService, FrontendProperties frontendProperties,
                              OpenIdProperties openIdProperties,
                              @Qualifier(ApiClientConfig.STEAM_COMMUNITY_API_CLIENT) RestClient restClient) {
        this.nonceService = nonceService;
        this.openIdValidator = openIdValidator;
        this.tokenService = tokenService;
        this.openIdProperties = openIdProperties;
        this.frontendProperties = frontendProperties;
        this.restClient = restClient;
    }

    private static void validateLoginState(String state) {
        if (state == null || !LOGIN_STATE_PATTERN.matcher(state).matches()) {
            throw new BadRequestException("INVALID_SIGN_IN_STATE", "Invalid sign-in state");
        }
    }

    public String constructLoginRedirect(String clientState) {
        validateLoginState(clientState);
        return constructRedirect(loginCallbackUrl(clientState));
    }

    public String constructLinkRedirect(long userId) {
        String linkToken = tokenService.generateToken(
                new SteamLinkRequestPayload(userId),
                openIdProperties.linkTokenTtl()
        );
        return constructRedirect(linkCallbackUrl(linkToken));
    }

    public String authenticateLogin(MultiValueMap<String, String> params) {
        String state = params.getFirst("state");
        validateLoginState(state);
        String steamId = verify(params, loginCallbackUrl(state));
        return tokenService.generateToken(new ApprovedAuthPayload(AuthSource.STEAM, steamId));
    }

    public String authenticateLink(MultiValueMap<String, String> params) {
        String linkToken = params.getFirst("link_token");
        SteamLinkRequestPayload linkRequest = tokenService.consumeToken(linkToken, SteamLinkRequestPayload.class);
        String steamId = verify(params, linkCallbackUrl(linkToken));
        return tokenService.generateToken(new ApprovedSteamLinkPayload(linkRequest.userId(), steamId));
    }

    private String constructRedirect(String returnUrl) {
        return UriComponentsBuilder
                .fromUriString(STEAM_OPENID_ENDPOINT + "/login")
                .queryParam("openid.ns", OPENID_NAMESPACE)
                .queryParam("openid.mode", OpenIdMode.CHECKID_SETUP.getValue())
                .queryParam("openid.return_to", returnUrl)
                .queryParam("openid.realm", realm())
                .queryParam(
                        "openid.identity",
                        OPENID_IDENTIFIER_SELECT
                )
                .queryParam(
                        "openid.claimed_id",
                        OPENID_IDENTIFIER_SELECT
                )
                .encode()
                .build()
                .toUriString();
    }

    private String verify(MultiValueMap<String, String> params, String expectedReturnTo) {
        openIdValidator.validateNamespace(params.getFirst("openid.ns"), OPENID_NAMESPACE);
        openIdValidator.validateMode(params.getFirst("openid.mode"));
        openIdValidator.validateEndpoint(
                params.getFirst("openid.op_endpoint"),
                STEAM_OPENID_ENDPOINT + "/login"
        );
        openIdValidator.validateReturnTo(params.getFirst("openid.return_to"), expectedReturnTo);
        validateSignedFields(params.getFirst("openid.signed"));

        String claimedId = params.getFirst("openid.claimed_id");
        if (!Objects.equals(claimedId, params.getFirst("openid.identity"))) {
            throw new IllegalArgumentException("OpenID 'claimed_id' and 'identity' differ");
        }
        String steamId = extractSteamId(claimedId);

        String nonce = params.getFirst("openid.response_nonce");
        openIdValidator.validateNonceAsBasic(nonce);
        if (nonceService.existByNonce(nonce, STEAM_OPENID_ENDPOINT)) {
            throw new IllegalArgumentException("OpenID nonce already used");
        }

        verifyWithSteam(params);

        if (!nonceService.tryRegister(nonce, STEAM_OPENID_ENDPOINT)) {
            throw new IllegalArgumentException("OpenID nonce already used");
        }
        return steamId;
    }

    private void validateSignedFields(String signed) {
        if (signed == null || signed.isBlank()) {
            throw new IllegalArgumentException("OpenID 'signed' cannot be empty");
        }
        Set<String> signedFields = Set.of(signed.split(","));
        if (!signedFields.containsAll(REQUIRED_SIGNED_FIELDS)) {
            throw new IllegalArgumentException("OpenID signature does not cover all identity fields: " + signed);
        }
    }

    private String realm() {
        return frontendProperties.url().toString();
    }

    private String loginCallbackUrl(String state) {
        return realm() + BASE_PATH + LOGIN_CALLBACK + "?state=" + state;
    }

    private String linkCallbackUrl(String linkToken) {
        return realm() + BASE_PATH + LINK_CALLBACK + "?link_token=" + linkToken;
    }

    private String extractSteamId(String claimedId) {
        if (claimedId == null || claimedId.isBlank()) {
            throw new IllegalArgumentException("Steam OpenID 'claimed_id' cannot be empty");
        }

        Matcher matcher = STEAM_CLAIMED_ID_PATTERN.matcher(claimedId);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Steam OpenID 'claimed_id'");
        }

        return matcher.group(1);
    }

    private void verifyWithSteam(MultiValueMap<String, String> params) {
        String response = sendAssertionToSteam(params);

        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("OpenID response cannot be empty");
        }

        Map<String, String> result = new HashMap<>();

        for (String line : response.split("\n")) {
            if (line.isBlank()) {
                throw new IllegalArgumentException("OpenID response is completely malformed");
            }

            int separator = line.indexOf(':');
            if (separator <= 0) {
                throw new IllegalArgumentException("OpenID response is partially malformed: " + line);
            }

            String key = line.substring(0, separator);
            String value = line.substring(separator + 1);

            if (result.putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException("Duplicate OpenID response key: " + key);
            }
        }

        openIdValidator.validateNamespace(result.get("ns"), OPENID_NAMESPACE);

        if (!"true".equals(result.get("is_valid"))) {
            throw new IllegalArgumentException("Steam rejected OpenID assertion");
        }
    }

    private String sendAssertionToSteam(MultiValueMap<String, String> params) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        params.forEach((name, values) -> {
            if (!name.startsWith("openid.")) {
                return;
            }

            if (values.size() != 1) {
                throw new IllegalArgumentException("Duplicate OpenID parameter: " + name);
            }

            form.add(name, values.getFirst());
        });

        form.set("openid.mode", "check_authentication");

        try {
            return restClient.post()
                    .uri(STEAM_OPENID_ENDPOINT + "/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw ExternalServiceException.of("STEAM_OPENID_ERROR", "Steam OpenID check failed", e);
        }
    }
}