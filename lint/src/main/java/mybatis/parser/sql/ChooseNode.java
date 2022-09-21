package mybatis.parser.sql;

import java.util.List;

public class ChooseNode implements SqlNode {
    private final SqlNode defaultSqlNode;
    private final List<SqlNode> ifSqlNodes;

    public ChooseNode(List<SqlNode> ifSqlNodes, SqlNode defaultSqlNode) {
        this.ifSqlNodes = ifSqlNodes;
        this.defaultSqlNode = defaultSqlNode;
    }

    @Override
    public boolean apply(SqlNodeVisitor visitor) {
        for (SqlNode sqlNode : ifSqlNodes) {
            if (sqlNode.apply(visitor)) {
                return true;
            }
        }
        if (defaultSqlNode != null) {
            defaultSqlNode.apply(visitor);
            return true;
        }
        return false;
    }
}
