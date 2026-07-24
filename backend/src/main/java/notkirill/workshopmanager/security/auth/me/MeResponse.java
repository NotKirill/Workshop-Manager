package notkirill.workshopmanager.security.auth.me;

import java.time.OffsetDateTime;

public record MeResponse(
        Long id,
        String name,
        OffsetDateTime timeCreated
) {
}
