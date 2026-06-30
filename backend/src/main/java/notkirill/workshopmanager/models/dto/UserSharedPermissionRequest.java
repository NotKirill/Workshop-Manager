package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record UserSharedPermissionRequest(
        @NotNull Long userProviderId,
        @NotNull Long userConsumerId,
        @NotNull Short permissionId
) {
}
