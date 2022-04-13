package mybatis.diagnostics.exception;

public class DatabaseObjectNameCheckException extends Exception{
    public DatabaseObjectNameCheckException() {
    }

    public DatabaseObjectNameCheckException(String message) {
        super(message);
    }

    public DatabaseObjectNameCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseObjectNameCheckException(Throwable cause) {
        super(cause);
    }

    public DatabaseObjectNameCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
