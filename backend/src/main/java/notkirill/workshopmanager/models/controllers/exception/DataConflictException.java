package notkirill.workshopmanager.models.controllers.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException() {
        super();
    }

    public DataConflictException(String message) {
        super(message);
    }
}
