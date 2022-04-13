package els.compiler;

public class UnexpectedCompileException extends RuntimeException{
    public UnexpectedCompileException() {
    }

    public UnexpectedCompileException(String message) {
        super(message);
    }

    public UnexpectedCompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedCompileException(Throwable cause) {
        super(cause);
    }

    public UnexpectedCompileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
