package mybatis.parser.sql.bound;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.ParameterMapChild;

import java.util.List;

public class StaticBoundSqlStatementSource implements BoundSqlStatementSource {

    private final String sql;
    private final List<ParameterMapChild> parameterMappings;
    private final Config configuration;

//    public StaticBoundSqlStatementSource(Config configuration, String sql) {
//        this(configuration, sql, null);
//    }

    public StaticBoundSqlStatementSource(Config configuration, String sql, List<ParameterMapChild> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSqlStatement getBoundSql(Object parameterObject) {
        return new BoundSqlStatement(configuration, sql, parameterMappings, parameterObject);
    }

    @Override
    public List<ParameterMapChild> getParameterMappings() {
        return this.parameterMappings;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }
}
