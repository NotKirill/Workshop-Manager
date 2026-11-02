package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.security.auth.AuthSource;

public record IdentityResponse(
        @JsonProperty("provider") AuthSource provider,
        @JsonProperty("key") String key
) {
}
