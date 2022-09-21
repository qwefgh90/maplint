package mybatis.parser.sql.bound;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.ParameterMapChild;
import mybatis.parser.sql.SqlNode;
import mybatis.parser.sql.SqlNodeVisitor;

import java.util.List;

/**
 * All parameters on the ${} notation are bound before it is prepared and parameterized.
 */
public class DynamicBoundSqlStatementSource implements BoundSqlStatementSource {

    private final Config configuration;
    private final SqlNode rootSqlNode;

    public DynamicBoundSqlStatementSource(Config configuration, SqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSqlStatement getBoundSql(Object parameterObject) {
        SqlNodeVisitor visitor = new SqlNodeVisitor(configuration, parameterObject);
        rootSqlNode.apply(visitor);
        BoundSqlStatementSourceBuilder sqlStatementBuilder = new BoundSqlStatementSourceBuilder(configuration);
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        BoundSqlStatementSource sqlSource = sqlStatementBuilder.build(visitor.getSql(), parameterType.getName(), visitor.getBindings());
        BoundSqlStatement boundSql = sqlSource.getBoundSql(parameterObject);
        visitor.getBindings().forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }

    @Override
    public List<ParameterMapChild> getParameterMappings() {
        Object parameterObject = new Object();
        SqlNodeVisitor context = new SqlNodeVisitor(configuration, parameterObject);
        rootSqlNode.apply(context);
        BoundSqlStatementSourceBuilder sqlSourceParser = new BoundSqlStatementSourceBuilder(configuration);
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        BoundSqlStatementSource sqlSource = sqlSourceParser.build(context.getSql(), parameterType.getName(), context.getBindings());
        return sqlSource.getParameterMappings();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
