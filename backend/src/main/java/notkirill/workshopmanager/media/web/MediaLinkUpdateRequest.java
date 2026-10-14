package notkirill.workshopmanager.media.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MediaLinkUpdateRequest(
        @JsonProperty("purpose") String purpose,
        @JsonProperty("hidden") Boolean hidden
) {
}
