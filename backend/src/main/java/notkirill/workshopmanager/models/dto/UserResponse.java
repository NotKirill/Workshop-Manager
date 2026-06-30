package notkirill.workshopmanager.models.dto;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String login,
        String email,
        String passwordHash,
        OffsetDateTime timeCreated,
        String name,
        short[] permissionsIds
) {
}
