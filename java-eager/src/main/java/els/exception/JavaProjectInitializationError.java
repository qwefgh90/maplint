package els.exception;

/**
 * @author qwefgh90
 */
public class JavaProjectInitializationError extends Exception{
    public JavaProjectInitializationError() {
    }

    public JavaProjectInitializationError(String message) {
        super(message);
    }

    public JavaProjectInitializationError(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaProjectInitializationError(Throwable cause) {
        super(cause);
    }

    public JavaProjectInitializationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
