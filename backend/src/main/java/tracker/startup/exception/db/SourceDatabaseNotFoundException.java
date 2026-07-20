package tracker.startup.exception.db;

public class SourceDatabaseNotFoundException extends RuntimeException {

    public SourceDatabaseNotFoundException(String message) {
        super(message);
    }

    public SourceDatabaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}