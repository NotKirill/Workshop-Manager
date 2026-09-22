package notkirill.workshopmanager.security.auth.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Clock;
import java.time.OffsetDateTime;

@Component
public class OpenIdValidator {
    private final Clock clock;
    private final OpenIdProperties openIdProperties;

    @Autowired
    public OpenIdValidator(Clock clock, OpenIdProperties openIdProperties) {
        this.clock = clock;
        this.openIdProperties = openIdProperties;
    }

    public void validateNamespace(String currentNamespace, String expectedNamespace) {
        if (expectedNamespace == null || expectedNamespace.isBlank()) {
            throw new IllegalArgumentException("Validation error: expected namespace cannot be empty");
        }

        if (currentNamespace == null || currentNamespace.isBlank()) {
            throw new IllegalArgumentException("OpenID 'ns' (namespace) cannot be empty");
        }

        if (!expectedNamespace.equals(currentNamespace)) {
            throw new IllegalArgumentException("Invalid OpenID namespace: " + currentNamespace);
        }
    }

    public void validateMode(String mode) {
        if (mode == null || mode.isBlank()) {
            throw new IllegalArgumentException("OpenID 'mode' cannot be empty");
        }

        if (!OpenIdMode.ID_RES.getValue().equals(mode)) {
            throw new IllegalArgumentException("OpenID 'mode' only can be 'id_res' at this stage, value: " + mode);
        }
    }

    public void validateReturnTo(String returnTo, String expectedReturnTo) {
        if (returnTo == null || returnTo.isBlank()) {
            throw new IllegalArgumentException("OpenID 'return_to' cannot be empty");
        }

        URI actualUri;
        try {
            actualUri = URI.create(returnTo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid OpenID 'return_to': " + returnTo, e);
        }

        if (!URI.create(expectedReturnTo).equals(actualUri)) {
            throw new IllegalArgumentException("Invalid OpenID 'return_to': " + returnTo);
        }
    }

    public void validateEndpoint(String currentEndpoint, String expectedEndpoint) {
        if (expectedEndpoint == null || expectedEndpoint.isBlank()) {
            throw new IllegalArgumentException("Validation error: expected endpoint cannot be empty");
        }

        if (currentEndpoint == null || currentEndpoint.isBlank()) {
            throw new IllegalArgumentException("OpenID 'op_endpoint' cannot be empty");
        }

        if (!expectedEndpoint.equals(currentEndpoint)) {
            throw new IllegalArgumentException("OpenID 'op_endpoint' is different: " + currentEndpoint);
        }
    }

    public void validateNonceAsBasic(String nonce) {
        if (nonce == null || nonce.isBlank()) {
            throw new IllegalArgumentException("OpenID nonce cannot be empty");
        }

        if (nonce.length() > 255) {
            throw new IllegalArgumentException("Invalid OpenID nonce");
        }

        int zIndex = nonce.indexOf('Z');
        if (zIndex == -1) {
            throw new IllegalArgumentException("Invalid OpenID nonce");
        }

        String timestampStr = nonce.substring(0, zIndex + 1);
        OffsetDateTime nonceTime = OffsetDateTime.parse(timestampStr);
        OffsetDateTime nowUtc = OffsetDateTime.now(clock);

        if (nonceTime.isBefore(nowUtc.minusMinutes(openIdProperties.nonce().maxAgeMinutes()))) {
            throw new IllegalArgumentException("OpenID nonce is too old");
        }

        if (nonceTime.isAfter(nowUtc.plusMinutes(openIdProperties.nonce().allowedFutureSkewMinutes()))) {
            throw new IllegalArgumentException("OpenID nonce is too new");
        }
    }
}
