package mybatis.diagnostics.exception;

public class TypeCompatibilityCheckException extends Exception{
    public TypeCompatibilityCheckException() {
    }

    public TypeCompatibilityCheckException(String message) {
        super(message);
    }

    public TypeCompatibilityCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeCompatibilityCheckException(Throwable cause) {
        super(cause);
    }

    public TypeCompatibilityCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
