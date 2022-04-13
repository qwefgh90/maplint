package mybatis.parser.sql.bound;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.ParameterMapChild;
import mybatis.parser.sql.BaseSqlNode;
import mybatis.parser.sql.DynamicContextCopy;

import java.util.List;

/**
 * All parameters on the ${} notation are bound before it is prepared and parameterized.
 */
public class DynamicBoundSqlStatementSource implements BoundSqlStatementSource {

    private final Config configuration;
    private final BaseSqlNode rootSqlNode;

    public DynamicBoundSqlStatementSource(Config configuration, BaseSqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSqlStatement getBoundSql(Object parameterObject) {
        DynamicContextCopy context = new DynamicContextCopy(configuration, parameterObject);
        rootSqlNode.apply(context);
        BoundSqlSourceBuilder sqlStatementBuilder = new BoundSqlSourceBuilder(configuration);
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        BoundSqlStatementSource sqlSource = sqlStatementBuilder.build(context.getSql(), parameterType.getName(), context.getBindings());
        BoundSqlStatement boundSql = sqlSource.getBoundSql(parameterObject);
        context.getBindings().forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }

    @Override
    public List<ParameterMapChild> getParameterMappings() {
        Object parameterObject = new Object();
        DynamicContextCopy context = new DynamicContextCopy(configuration, parameterObject);
        rootSqlNode.apply(context);
        BoundSqlSourceBuilder sqlSourceParser = new BoundSqlSourceBuilder(configuration);
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        BoundSqlStatementSource sqlSource = sqlSourceParser.build(context.getSql(), parameterType.getName(), context.getBindings());
        return sqlSource.getParameterMappings();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
