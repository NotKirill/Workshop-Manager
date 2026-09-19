package notkirill.workshopmanager.security.auth.provider.steam;

import jakarta.servlet.http.HttpServletRequest;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.ratelimit.ApiClientConfig;
import notkirill.workshopmanager.security.auth.ApprovedAuthPayload;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.security.auth.openid.OpenIdMode;
import notkirill.workshopmanager.security.auth.openid.OpenIdNonceService;
import notkirill.workshopmanager.security.auth.openid.OpenIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://www.openid.net/specs/openid-authentication-2_0-12.html
// Maybe TODO we can abstract it to OpenID 2.0 workflow with a discovery feature, but we don't need that right now
@Service
public class SteamOpenIdService {
    private static final String OPENID_NAMESPACE = "http://specs.openid.net/auth/2.0";
    private static final String OPENID_IDENTIFIER_SELECT = OPENID_NAMESPACE + "/identifier_select";

    private static final String STEAM_OPENID_ENDPOINT = "https://steamcommunity.com/openid";
    private static final Pattern STEAM_CLAIMED_ID_PATTERN =
            Pattern.compile("^https://steamcommunity\\.com/openid/id/(\\d+)$");

    private final OpenIdNonceService nonceService;
    private final OpenIdValidator openIdValidator;
    private final OneTimeTokenService tokenService;

    private final RestClient restClient;

    @Autowired
    public SteamOpenIdService(OpenIdNonceService nonceService, OpenIdValidator openIdValidator,
                              OneTimeTokenService tokenService,
                              @Qualifier(ApiClientConfig.STEAM_COMMUNITY_API_CLIENT) RestClient restClient) {
        this.nonceService = nonceService;
        this.openIdValidator = openIdValidator;
        this.tokenService = tokenService;
        this.restClient = restClient;
    }

    public String constructRedirect(String callbackPath) {
        String realm = ServletUriComponentsBuilder.fromCurrentContextPath()
                .replacePath(null)
                .toUriString();
        String returnUrl = realm + callbackPath;

        return UriComponentsBuilder
                .fromUriString(STEAM_OPENID_ENDPOINT + "/login")
                .queryParam("openid.ns", OPENID_NAMESPACE)
                .queryParam("openid.mode", OpenIdMode.CHECKID_SETUP.getValue())
                .queryParam("openid.return_to", returnUrl)
                .queryParam("openid.realm", realm)
                .queryParam(
                        "openid.identity",
                        OPENID_IDENTIFIER_SELECT
                )
                .queryParam(
                        "openid.claimed_id",
                        OPENID_IDENTIFIER_SELECT
                )
                .build()
                .toUriString();
    }

    public String authenticate(HttpServletRequest servletRequest) {
        validateOpenIdResponse(servletRequest);

        String steamId = extractSteamId(servletRequest.getParameter("openid.claimed_id"));
        validateNonceAsFull(servletRequest.getParameter("openid.response_nonce"));
        verifyWithSteam(servletRequest);

        return tokenService.generateToken(new ApprovedAuthPayload(AuthSource.STEAM, steamId));
    }

    private void validateOpenIdResponse(HttpServletRequest request) {
        openIdValidator.validateNamespace(request.getParameter("openid.ns"), OPENID_NAMESPACE);
        openIdValidator.validateMode(request.getParameter("openid.mode"));
        openIdValidator.validateReturnTo(request.getParameter("openid.return_to"));
        openIdValidator.validateEndpoint(
                request.getParameter("openid.op_endpoint"),
                STEAM_OPENID_ENDPOINT + "/login"
        );
//        String mode = request.getParameter("openid.mode");
//
//        if ("cancel".equals(mode)) {
////            return "redirect:/login?error=cancelled";
//        }
//
//        if (!"id_res".equals(mode)) {
////            return "redirect:/login?error=invalid_response";
//        }
    }

    private void validateNonceAsFull(String nonce) {
        openIdValidator.validateNonceAsBasic(nonce);

        if (nonceService.existByNonce(nonce, STEAM_OPENID_ENDPOINT)) {
            throw new IllegalArgumentException("OpenID nonce already used");
        }

        nonceService.create(nonce, STEAM_OPENID_ENDPOINT);
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

    private void verifyWithSteam(HttpServletRequest request) {
        String response = sendAssertionToSteam(request);

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

    private String sendAssertionToSteam(HttpServletRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        request.getParameterMap().forEach((name, values) -> {
            if (!name.startsWith("openid.")) {
                return;
            }

            if (values.length != 1) {
                throw new IllegalArgumentException("Duplicate OpenID parameter: " + name);
            }

            form.add(name, values[0]);
        });

        form.set("openid.mode", "check_authentication");

        return restClient.post()
                .uri(STEAM_OPENID_ENDPOINT + "/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(String.class);
    }
}