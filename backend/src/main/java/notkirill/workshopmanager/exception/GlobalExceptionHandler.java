package notkirill.workshopmanager.exception;

import jakarta.validation.ValidationException;
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

import java.util.NoSuchElementException;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorService errorService;

    @Autowired
    public GlobalExceptionHandler(ErrorService errorService) {
        this.errorService = errorService;
    }

    @ExceptionHandler({
            IllegalArgumentException.class, ValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, WebRequest request) {
        ErrorResponse response = errorService.handleError(log, ex, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler({
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, WebRequest request) {
        ErrorResponse response = errorService.handleError(log, ex, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler({
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleDefault(RuntimeException ex, WebRequest request) {
        ErrorResponse response = errorService.handleError(log, ex, HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(response.status()).body(response);
    }
}