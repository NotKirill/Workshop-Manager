package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record UserTagRequest(
        String displayName,
        @NotNull Long gameId
) {
}
