package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record GameCompatibilityRequest(
        @NotNull Long sourceGameId,
        @NotNull Long tagetGameId
) {
}