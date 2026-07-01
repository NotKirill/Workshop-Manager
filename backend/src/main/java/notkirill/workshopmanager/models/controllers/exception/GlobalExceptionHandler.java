package notkirill.workshopmanager.models.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ValidationException.class, DataConflictException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException exp) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exp.printStackTrace(pw);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exp.getMessage(),
                LocalDateTime.now(),
                sw.toString()
        );

        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler({
            NoSuchEntityException.class, PageNotFoundException.class, ResourceNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exp) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exp.printStackTrace(pw);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exp.getMessage(),
                LocalDateTime.now(),
                sw.toString()
        );

        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler({
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleDefault(RuntimeException exp) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exp.printStackTrace(pw);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exp.getMessage(),
                LocalDateTime.now(),
                sw.toString()
        );

        return ResponseEntity.status(response.status()).body(response);
    }
}