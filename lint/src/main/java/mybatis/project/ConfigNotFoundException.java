package mybatis.project;

public class ConfigNotFoundException extends Exception{
    String rootPath;

    public ConfigNotFoundException(String rootPath) {
        this.rootPath = rootPath;
    }

    public ConfigNotFoundException(String message, String rootPath) {
        super(message);
        this.rootPath = rootPath;
    }

    public ConfigNotFoundException(String message, Throwable cause, String rootPath) {
        super(message, cause);
        this.rootPath = rootPath;
    }

    public ConfigNotFoundException(Throwable cause, String rootPath) {
        super(cause);
        this.rootPath = rootPath;
    }

    public ConfigNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String rootPath) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.rootPath = rootPath;
    }
}
