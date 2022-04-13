package depreacted.db.mybatis.config.parser;

import depreacted.db.mybatis.MyBatisConfig;
import depreacted.db.mybatis.ParameterizedStatement;
import depreacted.db.mybatis.exception.MyBatisConfigParsingException;
import depreacted.db.validation.Validator;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class DefaultMyBatisConfig extends MyBatisConfig {
    private Configuration conf;
    private Environment env;

    DefaultMyBatisConfig(Configuration conf, Environment env) {
        super(conf, env);
        this.conf = conf;
        this.env = env;
    }

    /**
     * It uses the database connection to create the prepared statement.
     * The connections are needed in many places.
     * @param id
     * @param param
     * @return
     * @throws MyBatisConfigParsingException
     */
    @Override
    public ParameterizedStatement createStatement(String id, Object param) throws MyBatisConfigParsingException {
        var ms = conf.getMappedStatement(id);
        var tran = env.getTransactionFactory().newTransaction(env.getDataSource(), null, false);
        var exec = conf.newExecutor(tran, ExecutorType.SIMPLE);
        StatementHandler handler = conf.newStatementHandler(exec
                , ms
                , param
                , RowBounds.DEFAULT
                , null
                , null);
        var boundSQL = handler.getBoundSql();
        Statement stmt;
        Connection connection = null;
        try {
            connection = tran.getConnection();
            stmt = handler.prepare(connection, tran.getTimeout());
            handler.parameterize(stmt);
            var colIndex = stmt.toString().indexOf(':');
            var queryString = stmt.toString().substring(colIndex + 1).trim();
            return new ParameterizedStatement(ms, stmt, new Validator(queryString).parse(), boundSQL, queryString, handler);
        } catch (SQLException | JSQLParserException e) {
            throw new MyBatisConfigParsingException(e);
        }
    }
}
