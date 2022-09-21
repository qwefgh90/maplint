package mybatis.parser.sql;

public class StaticTextNode implements SqlNode {
    private final String text;

    public StaticTextNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(SqlNodeVisitor visitor) {
        visitor.appendSql(text);
        return true;
    }
}
