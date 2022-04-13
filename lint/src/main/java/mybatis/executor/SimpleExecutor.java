package mybatis.executor;

import mybatis.executor.statement.StatementHandler;
import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import mybatis.parser.model.MapperStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {
    public SimpleExecutor(Config configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public int doUpdate(MapperStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            Config configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
            stmt = prepareStatement(handler, ms.getStatementLog());
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    public <E> List<E> doQuery(MapperStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSqlStatement boundSql) throws SQLException {
        Statement stmt = null;
        try {
            Config configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
            stmt = prepareStatement(handler, ms.getStatementLog());
            return handler.query(stmt, resultHandler);
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    protected <E> Cursor<E> doQueryCursor(MapperStatement ms, Object parameter, RowBounds rowBounds, BoundSqlStatement boundSql) throws SQLException {
        Config configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
        Statement stmt = prepareStatement(handler, ms.getStatementLog());
        Cursor<E> cursor = handler.queryCursor(stmt);
        stmt.closeOnCompletion();
        return cursor;
    }

    @Override
    public List<BatchResult> doFlushStatements(boolean isRollback) {
        return Collections.emptyList();
    }

    private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
        Statement stmt;
        Connection connection = getConnection(statementLog);
        stmt = handler.prepare(connection, transaction.getTimeout());
        handler.parameterize(stmt);
        return stmt;
    }
}
