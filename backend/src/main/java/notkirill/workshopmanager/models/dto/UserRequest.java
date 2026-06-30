package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record UserRequest(
        @NotNull String login,
        String email,
        @NotNull String passwordHash,
        @NotNull OffsetDateTime timeCreated,
        @NotNull String name,
        short[] permissionsIds
) {
}
