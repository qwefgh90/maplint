package mybatis.executor.statement;

import mybatis.executor.Executor;
import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.MapperStatement;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.CallableStatementHandler;
import org.apache.ibatis.executor.statement.SimpleStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class RoutingStatementHandler implements StatementHandler{
    private final StatementHandler delegate;

    public RoutingStatementHandler(Executor executor, MapperStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSqlStatement boundSql) {

        switch (ms.getStatementType()) {
//            case STATEMENT:
//                delegate = new SimpleStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
//                break;
            case PREPARED:
                delegate = new PreparedStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
                break;
//            case CALLABLE:
//                delegate = new CallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
//                break;
            default:
                throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
        }

    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        return delegate.prepare(connection, transactionTimeout);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        delegate.parameterize(statement);
    }

    @Override
    public void batch(Statement statement) throws SQLException {
        delegate.batch(statement);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        return delegate.update(statement);
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        return delegate.query(statement, resultHandler);
    }

    @Override
    public <E> Cursor<E> queryCursor(Statement statement) throws SQLException {
        return delegate.queryCursor(statement);
    }

    @Override
    public BoundSqlStatement getBoundSql() {
        return delegate.getBoundSql();
    }

    @Override
    public ParameterHandler getParameterHandler() {
        return delegate.getParameterHandler();
    }
}
