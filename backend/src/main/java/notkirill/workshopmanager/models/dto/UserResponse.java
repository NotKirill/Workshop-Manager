package notkirill.workshopmanager.models.dto;

import java.time.OffsetDateTime;

// TODO close auth data
public record UserResponse(
        Long id,
        String passwordHash,
        OffsetDateTime timeCreated,
        String name,
        short[] permissionsIds
) {
}
