package mybatis.parser.sql;


import java.util.List;

public class MixedNode implements BaseSqlNode{

    private final List<BaseSqlNode> contents;

    public MixedNode(List<BaseSqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(BaseSqlNodeVisitor context) {
        contents.forEach(node -> node.apply(context));
        return true;
    }
}
