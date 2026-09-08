package notkirill.workshopmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

public class ExternalServiceException extends ApiException {
    private final boolean transientFailure;

    public ExternalServiceException(String code, String message, boolean transientFailure) {
        super(code, message);
        this.transientFailure = transientFailure;
    }

    public ExternalServiceException(String code, String message, Throwable cause, boolean transientFailure) {
        super(code, message, cause);
        this.transientFailure = transientFailure;
    }

    public static ExternalServiceException of(String code, String label, RestClientException error) {
        if (error instanceof RestClientResponseException response) {
            int status = response.getStatusCode().value();
            boolean transientFailure = status == 429 || status >= 500;
            return new ExternalServiceException(code, label + ": HTTP " + status, error, transientFailure);
        }
        return new ExternalServiceException(code, label + ": " + error.getClass().getSimpleName(), error, true);
    }

    public boolean isTransientFailure() {
        return transientFailure;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_GATEWAY;
    }
}
