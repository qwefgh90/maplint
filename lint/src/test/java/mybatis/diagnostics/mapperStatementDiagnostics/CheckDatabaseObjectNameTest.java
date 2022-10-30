package mybatis.diagnostics.mapperStatementDiagnostics;

import mybatis.diagnostics.MapperStatementDiagnostics;
import mybatis.diagnostics.exception.DatabaseObjectNameCheckException;
import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import org.apache.ibatis.session.ExecutorType;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

public class CheckDatabaseObjectNameTest {
    Logger logger = LoggerFactory.getLogger(CheckDatabaseObjectNameTest.class);

    static Config config;
    Connection connection;

    @AfterEach
    void clean() throws SQLException , DatabaseObjectNameCheckException {
        if(connection!= null)
            connection.close();
    }

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, DatabaseObjectNameCheckException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var ddl = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1/src/main/resources/db/Tables.ddl").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root, "h2");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        config = parser.parse();
        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        var connection = transaction.getConnection();
        var pstmt = connection.prepareStatement(Files.readString(ddl));
        pstmt.execute();
        connection.close();
    }

    @Nested
    @DisplayName("Insert Into Statement")
    class InsertIntoStatementTest {
        @Test
        void test() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.InsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }

        @Test
        void test1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info(result.toString());
        }
        @Test
        void test2() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info(result.toString());
        }
    }

    @Nested
    @DisplayName("Insert Select Statement")
    class InsertSelectStatementTest {
        @Test
        void test() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.InsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info(result.toString());
        }
        @Test
        void test1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info(result.toString());
        }
        @Test
        void test2() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 3);
            logger.info(result.toString());
        }
        @Test
        void test3() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info(result.toString());
        }

        @Test
        void test4() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info(result.toString());
        }
    }

    @Nested
    @DisplayName("Select Join Statement")
    class SelectJoinStatementTest {
        @Test
        void test() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.SelectJoinStatement");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }

        @Test
        void test1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 8);
            logger.info(result.toString());
        }
        @Test
        void test2() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info(result.toString());
        }
        @Test
        void test3() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 7);
            logger.info(result.toString());
        }
        @Test
        void test4() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 6);
            logger.info(result.toString());
        }
    }

    @Nested
    @DisplayName("Select Union Statement")
    class SelectUnionStatementTest {
        @Test
        void test() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.SelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 8);
        }
    }

    @Nested
    @DisplayName("Select Into Statement")
    class SelectIntoStatementTest {
        @Test
        void test() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.SelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 4);
        }
        @Test
        void test2() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectIntoStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
        }
    }

    @Nested
    @DisplayName("Update Statement")
    class UpdateStatementTest {
        @Test
        void test() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.updateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info(result.toString());
        }

        @Test
        void test1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongUpdateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 3);
            logger.info(result.toString());
        }

        @Test
        void test2() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongUpdateContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info(result.toString());
        }
    }

    @Nested
    @DisplayName("Delete Statement")
    class DeleteStatementTest {
        @Test
        void test() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.deleteContent");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info(result.toString());
        }

        @Test
        void test1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongDeleteContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info(result.toString());
        }

        @Test
        void test2() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongDeleteContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info(result.toString());
        }
    }
}
