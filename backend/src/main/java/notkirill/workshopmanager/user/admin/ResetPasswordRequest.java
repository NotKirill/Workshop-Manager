package notkirill.workshopmanager.user.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @JsonProperty("password") @NotBlank String password
) {
}
