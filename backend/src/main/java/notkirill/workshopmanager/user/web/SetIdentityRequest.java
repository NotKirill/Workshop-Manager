package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetIdentityRequest(
        @JsonProperty("key") @NotBlank @Size(max = 254) String key
) {
}
