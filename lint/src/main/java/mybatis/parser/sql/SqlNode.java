package mybatis.parser.sql;

public interface SqlNode {
    /**
     * Visit the SqlNode and accumulate results into the visitor
     * @param visitor
     * @return
     */
    boolean apply(SqlNodeVisitor visitor);
}
