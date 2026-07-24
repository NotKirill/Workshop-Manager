package notkirill.workshopmanager.user.dto;

import java.time.OffsetDateTime;

public record UserResponseDto(
        Long id,
        OffsetDateTime timeCreated,
        String name
) {
}
