package db.mybatis.exception;

public class MyBatisConfigParsingException extends Exception{
    public MyBatisConfigParsingException() {
    }

    public MyBatisConfigParsingException(String message) {
        super(message);
    }

    public MyBatisConfigParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyBatisConfigParsingException(Throwable cause) {
        super(cause);
    }

    public MyBatisConfigParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
