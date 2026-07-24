package notkirill.workshopmanager.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String code,
        String error_uuid,
        String apiPath,
        LocalDateTime timestamp
) {
}
