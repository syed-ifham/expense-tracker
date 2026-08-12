package tracker.reload.exception;

public class SourceFileLockedException extends RuntimeException {
    public SourceFileLockedException(String msg) {
        super(msg);
    }
}
