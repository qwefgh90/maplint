package mybatis.parser.sql;

import org.apache.ibatis.scripting.xmltags.OgnlCache;

public class VarDeclNode implements BaseSqlNode {

    private final String name;
    private final String expression;

    public VarDeclNode(String var, String exp) {
        name = var;
        expression = exp;
    }

    @Override
    public boolean apply(DynamicContextCopy context) {
        final Object value = OgnlCache.getValue(expression, context.getBindings());
        context.bind(name, value);
        return true;
    }
}
