package mybatis.parser.sql.bound;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.ParameterMapChild;
import mybatis.parser.sql.BaseSqlNode;
import mybatis.parser.sql.BaseSqlNodeVisitor;

import java.util.HashMap;
import java.util.List;

public class RawBoundSqlStatementSource implements BoundSqlStatementSource {
    private final BoundSqlStatementSource internalSqlSource;

    public RawBoundSqlStatementSource(Config configuration, BaseSqlNode rootSqlNode, String parameterType) {
        this(configuration, getSql(configuration, rootSqlNode), parameterType);
    }

    public RawBoundSqlStatementSource(Config configuration, String sql, String parameterType) {
        BoundSqlStatementSourceBuilder sqlSourceBuilder = new BoundSqlStatementSourceBuilder(configuration);
        internalSqlSource = sqlSourceBuilder.build(sql, parameterType, new HashMap<>());
    }

    private static String getSql(Config configuration, BaseSqlNode rootSqlNode) {
        BaseSqlNodeVisitor context = new BaseSqlNodeVisitor(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }

    @Override
    public BoundSqlStatement getBoundSql(Object parameterObject) {
        return internalSqlSource.getBoundSql(parameterObject);
    }

    @Override
    public List<ParameterMapChild> getParameterMappings() {
        return this.internalSqlSource.getParameterMappings();
    }

    @Override
    public boolean isDynamic() {
        return false;
    }
}
