package mybatis.parser.sql;

import java.util.List;

public class ChooseNode implements BaseSqlNode {
    private final BaseSqlNode defaultSqlNode;
    private final List<BaseSqlNode> ifSqlNodes;

    public ChooseNode(List<BaseSqlNode> ifSqlNodes, BaseSqlNode defaultSqlNode) {
        this.ifSqlNodes = ifSqlNodes;
        this.defaultSqlNode = defaultSqlNode;
    }

    @Override
    public boolean apply(BaseSqlNodeVisitor context) {
        for (BaseSqlNode sqlNode : ifSqlNodes) {
            if (sqlNode.apply(context)) {
                return true;
            }
        }
        if (defaultSqlNode != null) {
            defaultSqlNode.apply(context);
            return true;
        }
        return false;
    }
}
