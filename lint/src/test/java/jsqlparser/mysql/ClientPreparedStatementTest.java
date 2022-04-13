package jsqlparser.mysql;

import mybatis.executor.statement.StatementHandler;
import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.feature.DatabaseType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClientPreparedStatementTest {
    Logger logger = LoggerFactory.getLogger(ClientPreparedStatementTest.class);

    static Config config;
    Connection connection;

    @AfterEach
    void clean() throws SQLException {
        if(connection!= null)
            connection.close();
    }

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root);
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        config = parser.parse();
    }

    @Test
    void failToMakeClientPreparedStatementWithoutConnection() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        Assertions.assertThrows(com.mysql.cj.jdbc.exceptions.CommunicationsException.class, () -> {
            var env = config.getEnvironment();
            var manager = env.getTransactionManager();
            var transaction = manager
                    .getTransactionFactory()
                    .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
            var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
            connection = transaction.getConnection();
//        var mapper = config.getMappedStatement("db.BlogMapper.createTableIfNotExist");
//        var pstmt = connection.prepareStatement(mapper.getSqlSource().getBoundSql(new HashMap()).getSql());
//        pstmt.execute();

            StatementHandler handler = config.newStatementHandler(exec,
                    config.getMappedStatement("db.BlogMapper.insertAuthor"),
                    Map.of("id", 0, "name", "창원"), RowBounds.DEFAULT, null, null);

            var stmt = handler.prepare(connection, transaction.getTimeout());
            handler.parameterize(stmt);
            var stmtToString = stmt.toString();
            var executableSql = stmtToString.substring(stmtToString.indexOf(':') + 1).trim();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());

        });
    }
}
