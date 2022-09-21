package mybatis.parser.sql;

import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator;

public class IfNode implements SqlNode {

    private final ExpressionEvaluator evaluator;
    private final String test;
    private final SqlNode contents;

    public IfNode(SqlNode contents, String test) {
        this.test = test;
        this.contents = contents;
        this.evaluator = new ExpressionEvaluator();
    }

    @Override
    public boolean apply(SqlNodeVisitor visitor) {
        if (evaluator.evaluateBoolean(test, visitor.getBindings())) {
            contents.apply(visitor);
            return true;
        }
        return false;
    }

}
