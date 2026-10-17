package notkirill.workshopmanager.content.fetch;

import notkirill.workshopmanager.content.enums.ContentJobError;

public class ContentFetchException extends RuntimeException {
    private final ContentJobError error;
    private final boolean retryable;

    public ContentFetchException(ContentJobError error, String message, boolean retryable) {
        super(message);
        this.error = error;
        this.retryable = retryable;
    }

    public ContentFetchException(ContentJobError error, String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.error = error;
        this.retryable = retryable;
    }

    public ContentJobError getError() {
        return error;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
