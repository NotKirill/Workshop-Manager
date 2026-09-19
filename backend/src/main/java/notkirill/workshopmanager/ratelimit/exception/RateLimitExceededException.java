package notkirill.workshopmanager.ratelimit.exception;

import notkirill.workshopmanager.exception.ApiException;
import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends ApiException {
    public RateLimitExceededException(String code, String message) {
        super(code, message);
    }

    public RateLimitExceededException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
