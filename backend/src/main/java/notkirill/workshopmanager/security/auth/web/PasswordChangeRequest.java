package notkirill.workshopmanager.security.auth.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
        @JsonProperty("login") @NotBlank String login,
        @JsonProperty("current_password") @NotBlank String currentPassword,
        @JsonProperty("new_password") @NotBlank String newPassword
) {
}
