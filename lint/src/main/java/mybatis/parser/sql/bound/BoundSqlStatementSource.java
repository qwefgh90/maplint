package mybatis.parser.sql.bound;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.ParameterMapChild;

import java.util.List;

public interface BoundSqlStatementSource {
    BoundSqlStatement getBoundSql(Object parameterObject);
    List<ParameterMapChild> getParameterMappings();
    boolean isDynamic();
}
