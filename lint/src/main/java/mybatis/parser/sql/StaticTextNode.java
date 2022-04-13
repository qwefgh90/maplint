package mybatis.parser.sql;

public class StaticTextNode implements BaseSqlNode {
    private final String text;

    public StaticTextNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContextCopy context) {
        context.appendSql(text);
        return true;
    }
}
