package mybatis.parser.model;

public class NotationToken {
    private String token;
    private int start;

    public NotationToken(String token, int start) {
        this.token = token;
        this.start = start;
    }

    public String getToken() {
        return token;
    }

    public int getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "NotationToken{" +
                "token='" + token + '\'' +
                ", start=" + start +
                '}';
    }
}
