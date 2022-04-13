package mybatis.parser.exception;

public class RegistrationNotAllowed extends Exception{
    public RegistrationNotAllowed() {
    }

    public RegistrationNotAllowed(String message) {
        super(message);
    }

    public RegistrationNotAllowed(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationNotAllowed(Throwable cause) {
        super(cause);
    }

    public RegistrationNotAllowed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
