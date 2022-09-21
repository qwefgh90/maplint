package mybatis.parser.sql;

import org.apache.ibatis.scripting.xmltags.OgnlCache;

public class VarDeclNode implements SqlNode {

    private final String name;
    private final String expression;

    public VarDeclNode(String var, String exp) {
        name = var;
        expression = exp;
    }

    @Override
    public boolean apply(SqlNodeVisitor visitor) {
        final Object value = OgnlCache.getValue(expression, visitor.getBindings());
        visitor.bind(name, value);
        return true;
    }
}
