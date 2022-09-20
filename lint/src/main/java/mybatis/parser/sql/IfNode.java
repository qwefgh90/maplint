package mybatis.parser.sql;

import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator;

public class IfNode implements  BaseSqlNode{

    private final ExpressionEvaluator evaluator;
    private final String test;
    private final BaseSqlNode contents;

    public IfNode(BaseSqlNode contents, String test) {
        this.test = test;
        this.contents = contents;
        this.evaluator = new ExpressionEvaluator();
    }

    @Override
    public boolean apply(BaseSqlNodeVisitor context) {
        if (evaluator.evaluateBoolean(test, context.getBindings())) {
            contents.apply(context);
            return true;
        }
        return false;
    }

}
