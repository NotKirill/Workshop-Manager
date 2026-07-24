package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.ApiException;
import notkirill.workshopmanager.exception.ErrorResponse;
import notkirill.workshopmanager.exception.ErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenExceptionHandler.class);

    private final ErrorService errorService;

    @Autowired
    public RefreshTokenExceptionHandler(ErrorService errorService) {
        this.errorService = errorService;
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleExpired(ApiException ex, WebRequest request) {
        ErrorResponse response = errorService.handleError(log, ex, HttpStatus.UNAUTHORIZED, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(RefreshTokenRevokedException.class)
    public ResponseEntity<ErrorResponse> handleRevoked(ApiException ex, WebRequest request) {
        ErrorResponse response = errorService.handleError(log, ex, HttpStatus.UNAUTHORIZED, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(RefreshTokenReusedException.class)
    public ResponseEntity<ErrorResponse> handleReuse(ApiException ex, WebRequest request) {
        ErrorResponse response = errorService.handleError(log, ex, HttpStatus.UNAUTHORIZED, request);
        return ResponseEntity.status(response.status()).body(response);
    }
}