package notkirill.workshopmanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class ErrorService {
    private static final String INTERNAL_CODE = "INTERNAL_ERROR";

    private final Clock clock;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Autowired
    public ErrorService(Clock clock) {
        this.clock = clock;
    }

    private static String defaultCode(Throwable ex, HttpStatus status) {
        if (ex instanceof ApiException apiEx) {
            return apiEx.getCode();
        }
        return status.is5xxServerError() ? INTERNAL_CODE : status.name();
    }

    private static String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    public ErrorResponse handleError(Logger log, Throwable ex, HttpStatus status, WebRequest request) {
        return handleError(log, ex, status, defaultCode(ex, status), request, null);
    }

    public ErrorResponse handleError(Logger log, Throwable ex, HttpStatus status, String code, WebRequest request,
                                     Map<String, String> fieldErrors) {
        return buildResponse(log, ex, status, code, extractPath(request), fieldErrors);
    }

    public void writeError(Logger log, Throwable ex, HttpStatus status, String code, HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        ErrorResponse body = buildResponse(log, ex, status, code, request.getRequestURI(), null);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(body));
    }

    private ErrorResponse buildResponse(Logger log, Throwable ex, HttpStatus status, String code, String apiPath,
                                        Map<String, String> fieldErrors) {
        UUID errorUuid = UUID.randomUUID();
        if (status.is5xxServerError()) {
            log.error(
                    "Error UUID: [{}], Path: {}, Code: {}, Message: {}", errorUuid, apiPath, code,
                    ex != null ? ex.getMessage() : null, ex
            );
        } else {
            log.warn(
                    "Error UUID: [{}], Path: {}, Status: {}, Code: {}, Message: {}",
                    errorUuid, apiPath, status.value(), code, ex != null ? ex.getMessage() : null
            );
        }

        return new ErrorResponse(
                status.value(),
                code,
                fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors,
                errorUuid.toString(),
                apiPath,
                OffsetDateTime.now(clock)
        );
    }
}
