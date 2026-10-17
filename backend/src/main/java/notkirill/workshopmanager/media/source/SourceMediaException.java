package notkirill.workshopmanager.media.source;

public class SourceMediaException extends RuntimeException {
    private final boolean permanent;

    public SourceMediaException(String message, boolean permanent) {
        super(message);
        this.permanent = permanent;
    }

    public SourceMediaException(String message, Throwable cause, boolean permanent) {
        super(message, cause);
        this.permanent = permanent;
    }

    public boolean isPermanent() {
        return permanent;
    }
}
