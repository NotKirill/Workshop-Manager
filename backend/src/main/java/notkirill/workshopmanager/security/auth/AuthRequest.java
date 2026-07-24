package notkirill.workshopmanager.security.auth;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank String login,
        @Nullable String password
) {
}
