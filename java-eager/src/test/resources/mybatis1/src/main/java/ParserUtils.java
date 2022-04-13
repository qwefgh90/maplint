import net.sf.jsqlparser.util.validation.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;;
import net.sf.jsqlparser.util.validation.metadata.*;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ParserUtils {
    Logger logger = LoggerFactory.getLogger(ParserUtils.class);

    Configuration conf;
    Transaction tran;
    Executor exec;
    public ParserUtils(Configuration conf, Transaction tran, Executor exec){
        this.conf = conf;
        this.tran = tran;
        this.exec = exec;
    }

    public ParserUtils(InputStream inputStream){
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);
        conf = parser.parse();
        var env = conf.getEnvironment();
        var transactionFactory = env.getTransactionFactory();
        tran = transactionFactory.newTransaction(env.getDataSource(), null, false);
        exec = conf.newExecutor(tran, ExecutorType.SIMPLE);
    }

    /**
     * jdbc:mysql://...?useServerPrepStmts=false
     *
     * @param ms
     * @param param
     */
    String createSQL(MappedStatement ms, Object param) throws SQLException {
        RoutingStatementHandler handler = (RoutingStatementHandler) conf.newStatementHandler(exec
                , ms
                , param
                , RowBounds.DEFAULT
                , null
                , null);
        Statement stmt;
        var boundSQL = handler.getBoundSql();
        logger.info("boundSQL: " + boundSQL.getSql());
        logger.info("boundSQL: " + boundSQL.getParameterMappings());
        logger.info("boundSQL: " + boundSQL.getParameterObject());

        var connection = tran.getConnection();
        stmt = handler.prepare(connection, tran.getTimeout());
        handler.parameterize(stmt);
        var colIndex = stmt.toString().indexOf(':');
        logger.info("parameterized SQL: " + stmt.toString());
        return stmt.toString().substring(colIndex + 1).trim();
    }

    net.sf.jsqlparser.statement.Statement parse(MappedStatement ms, Object param) throws SQLException, JSQLParserException {
        var stmt = createSQL(ms, param);
        var parseStatement = CCJSqlParserUtil.parse(stmt);
        return parseStatement;
    }

    net.sf.jsqlparser.statement.Statement parse(String stmt) throws SQLException, JSQLParserException {
        var parseStatement = CCJSqlParserUtil.parse(stmt);
        return parseStatement;
    }

    ExtendedJdbcDatabaseMetaDataCapability newExtendedCapability() throws SQLException {
        return new ExtendedJdbcDatabaseMetaDataCapability(tran.getConnection(), NamesLookup.NO_TRANSFORMATION);
    }

    List<ValidationError> validate(String stmt, ValidationCapability... capability) throws SQLException {
        Validation validation = new Validation(Arrays.asList(capability), stmt);
        List<ValidationError> errors = validation.validate();
        return errors;
    }

    List<ValidationError> validate(String stmt) throws SQLException {
        Validation validation = new Validation(Arrays.asList(newExtendedCapability()), stmt);
        List<ValidationError> errors = validation.validate();
        return errors;
    }

//    ResultSetMetaData getMetaDataOfColumn(String name) throws SQLException {
//        Map<Named, Boolean> results = new HashMap<>();
//        Map<Named, Boolean> m = Collections.unmodifiableMap(results);
//        return new ExtendedJdbcDatabaseMetaDataCapability(tran.getConnection(), NamesLookup.NO_TRANSFORMATION).metaData(m, new Named(NamedObject.column, name));
//    }
}
