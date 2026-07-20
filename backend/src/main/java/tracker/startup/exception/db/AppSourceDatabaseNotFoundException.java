package tracker.startup.exception.db;

public class AppSourceDatabaseNotFoundException extends RuntimeException {
    public AppSourceDatabaseNotFoundException(String message) {
        super(message);
    }

    public AppSourceDatabaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
