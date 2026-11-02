package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @JsonProperty("current_password") String currentPassword,
        @JsonProperty("new_password") @NotBlank String newPassword,
        @JsonProperty("refresh_token") String refreshToken
) {
}
