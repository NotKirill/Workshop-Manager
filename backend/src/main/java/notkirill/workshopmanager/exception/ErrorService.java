package notkirill.workshopmanager.exception;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ErrorService {
    private final Clock clock;

    @Autowired
    public ErrorService(Clock clock) {
        this.clock = clock;
    }

    public ErrorResponse handleError(Logger log, Throwable ex, HttpStatus status, WebRequest request) {
        String apiPath = request.getDescription(false).replace("uri=", "");
        UUID errorUuid = UUID.randomUUID();
        log.error("Error UUID: [{}], Message: {}", errorUuid, ex.getMessage(), ex);

        return new ErrorResponse(
                status.value(),
                (ex instanceof ApiException apiEx)
                        ? apiEx.getCode()
                        : ex.getClass().getSimpleName(),
                errorUuid.toString(),
                apiPath,
                LocalDateTime.now(clock)
        );
    }
}
