package mybatis.project;

public class MyBatisProjectInitializationException extends Exception{
    public MyBatisProjectInitializationException() {
    }

    public MyBatisProjectInitializationException(String message) {
        super(message);
    }

    public MyBatisProjectInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyBatisProjectInitializationException(Throwable cause) {
        super(cause);
    }

    public MyBatisProjectInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
