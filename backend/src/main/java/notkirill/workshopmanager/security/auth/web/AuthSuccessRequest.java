package notkirill.workshopmanager.security.auth.web;

import jakarta.validation.constraints.NotBlank;

public record AuthSuccessRequest(
        @NotBlank String ott
) {
}