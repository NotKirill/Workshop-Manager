package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

// TODO close auth data
public record UserRequest(
        @NotNull String passwordHash,
        @NotNull OffsetDateTime timeCreated,
        @NotNull String name,
        short[] permissionsIds
) {
}
