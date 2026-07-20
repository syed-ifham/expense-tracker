package tracker.startup.exception.db;

public class SourceFileLockedException extends RuntimeException {
    public SourceFileLockedException(String msg) {
        super(msg);
    }
}
