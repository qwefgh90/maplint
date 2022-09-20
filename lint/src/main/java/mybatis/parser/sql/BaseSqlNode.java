package mybatis.parser.sql;

public interface BaseSqlNode {
    boolean apply(BaseSqlNodeVisitor context);
}
