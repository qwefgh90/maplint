package mybatis.parser.sql;


import java.util.List;

public class MixedNode implements SqlNode {

    private final List<SqlNode> contents;

    public MixedNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(SqlNodeVisitor visitor) {
        contents.forEach(node -> node.apply(visitor));
        return true;
    }
}
