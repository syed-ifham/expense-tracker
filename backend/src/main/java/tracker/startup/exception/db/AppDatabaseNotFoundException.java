package tracker.startup.exception.db;

public class AppDatabaseNotFoundException extends RuntimeException {
    public AppDatabaseNotFoundException(String message) {
        super(message);
    }

    public AppDatabaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
