package notkirill.workshopmanager.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorService errorService;

    @Autowired
    public GlobalExceptionHandler(ErrorService errorService) {
        this.errorService = errorService;
    }

    private static String toJsonName(String javaPath) {
        return javaPath.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        return respond(ex, ex.getStatus(), ex.getCode(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(toJsonName(error.getField()), error.getCode());
        }
        return respond(ex, HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", request, fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> fieldErrors.putIfAbsent(
                violation.getPropertyPath().toString(),
                violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
        ));
        return respond(ex, HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", request, fieldErrors);
    }

    @ExceptionHandler({
            ValidationException.class, HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class, MissingRequestHeaderException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, WebRequest request) {
        return respond(ex, HttpStatus.BAD_REQUEST, "BAD_REQUEST", request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex, WebRequest request) {
        return respond(ex, HttpStatus.NOT_FOUND, "NOT_FOUND", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(Exception ex, WebRequest request) {
        return respond(ex, HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", request, null);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountUnavailable(AccountStatusException ex, WebRequest request) {
        String code = ex instanceof LockedException ? "ACCOUNT_LOCKED" : "ACCOUNT_DISABLED";
        return respond(ex, HttpStatus.FORBIDDEN, code, request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(AuthenticationException ex, WebRequest request) {
        return respond(ex, HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex, WebRequest request) {
        return respond(ex, HttpStatus.FORBIDDEN, "FORBIDDEN", request, null);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, PessimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleConflict(Exception ex, WebRequest request) {
        return respond(ex, HttpStatus.CONFLICT, "DATA_CONFLICT", request, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException ex, WebRequest request) {
        return respond(ex, HttpStatus.CONTENT_TOO_LARGE, "UPLOAD_TOO_LARGE", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefault(Exception ex, WebRequest request) {
        if (ex instanceof org.springframework.web.ErrorResponse springError
                && springError.getStatusCode().is4xxClientError()) {
            HttpStatus status = HttpStatus.valueOf(springError.getStatusCode().value());
            return respond(ex, status, status.name(), request, null);
        }
        return respond(ex, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", request, null);
    }

    private ResponseEntity<ErrorResponse> respond(Exception ex, HttpStatus status, String code, WebRequest request,
                                                  Map<String, String> fieldErrors) {
        ErrorResponse response = errorService.handleError(log, ex, status, code, request, fieldErrors);
        return ResponseEntity.status(response.status()).body(response);
    }
}
