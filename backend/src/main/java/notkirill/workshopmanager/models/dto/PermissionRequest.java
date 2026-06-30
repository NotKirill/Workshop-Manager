package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record PermissionRequest(
        @NotNull String name,
        String description
) {
}
