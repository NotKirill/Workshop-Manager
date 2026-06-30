package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record UserGroupRequest(
        @NotNull Long userId,
        @NotNull Long gameId,
        @NotNull String name
) {
}
