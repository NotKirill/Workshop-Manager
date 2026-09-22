package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LinkSteamRequest(
        @JsonProperty("ott") @NotBlank String ott
) {
}
