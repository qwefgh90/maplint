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
        var server = new MyBatisProjectService(root, "h2");
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
        void testN1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.InsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
        }

        @Test
        void testP1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info("TP: {}, FP: {}", 2, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
        @Test
        void testP2() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
    }

    @Nested
    @DisplayName("Insert Select Statement")
    class InsertSelectStatementTest {
        @Test
        void testN1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.InsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
        }

        @Test
        void testP1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            int tpCount = 0;
            if(2 ==
                    result.build().getErrorList().stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 2
                    ).count())
                tpCount += 2;
            logger.info("TP: {}, FP: {}", tpCount, result.build().getErrorList().size() - tpCount);
            logger.info("TN: {}, FN: {}", 0, 0);
            Assertions.assertEquals(2, result.build().getErrorList().size());
        }

        @Test
        void testP2() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            int tpCount = 0;
            int fnCount = 0;

            if(1 ==
                    result.build().getErrorList().stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 3
                    ).count())
                tpCount++;
            else
                fnCount++;
            if(1 ==
                    result.build().getErrorList().stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 4
                    ).count())
                tpCount++;
            else
                fnCount++;
            if(1 ==
                    result.build().getErrorList().stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 5
                    ).count())
                tpCount++;
            else
                fnCount++;
            logger.info("TP: {}, FP: {}", tpCount, result.build().getErrorList().size() - tpCount);
            logger.info("TN: {}, FN: {}", 0, fnCount);
            Assertions.assertEquals(3, result.build().getErrorList().size());
        }
        @Test
        void testP3() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }

        @Test
        void testP4() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongInsertSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
    }

    @Nested
    @DisplayName("Select Join Statement")
    class SelectJoinStatementTest {
        @Test
        void testN1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.SelectJoinStatement");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
        }

        @Test
        void testP1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 8);
            logger.info("TP: {}, FP: {}", 8, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
        @Test
        void testP2() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
        @Test
        void testP3() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 7);
            logger.info("TP: {}, FP: {}", 7, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
        @Test
        void testP4() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 6);
            logger.info("TP: {}, FP: {}", 6, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
    }

    @Nested
    @DisplayName("Select Union Statement")
    class SelectUnionStatementTest {
        @Test
        void testN1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.SelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
        }

        @Test
        void testP1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            var errorList = result.build().getErrorList();
            //
            Assertions.assertEquals(2,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 2
                    ).count());
            Assertions.assertEquals(1,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 3
                    ).count());
            Assertions.assertEquals(1,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 4
                    ).count());
            Assertions.assertEquals(1,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 13
                    ).count());
            Assertions.assertEquals(1,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 14
                    ).count());
            Assertions.assertEquals(1,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 15
                    ).count());
            Assertions.assertEquals(1,
                    errorList.stream().filter(err ->
                            err.getContext().getSource().sourcePosition.beginLine == 16
                    ).count());
            Assertions.assertEquals(8, result.build().getErrorList().size());
        }
    }

    @Nested
    @DisplayName("Select Into Statement")
    class SelectIntoStatementTest {
        @Test
        void testN1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.SelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
        }
        @Test
        void testP1() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 4);
            logger.info("TP: {}, FP: {}", 4, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
        }
        @Test
        void testP2() throws SQLException, DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.WrongSelectIntoStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
        }
    }

    @Nested
    @DisplayName("Update Statement")
    class UpdateStatementTest {
        @Test
        void testN1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.updateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);

        }

        @Test
        void testP1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongUpdateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 3);
            logger.info("TP: {}, FP: {}", 3, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }

        @Test
        void testP2() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongUpdateContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info("TP: {}, FP: {}", 2, 0);
            logger.info("TN: {}, FN: {}", 0, 0);

        }
    }

    @Nested
    @DisplayName("Delete Statement")
    class DeleteStatementTest {
        @Test
        void testN1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.deleteContent");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);

        }

        @Test
        void testP1() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongDeleteContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
            logger.info("TP: {}, FP: {}", 2, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
        }

        @Test
        void testP2() throws SQLException , DatabaseObjectNameCheckException {
            var stmt = config.getMappedStatement("db.WrongObjectName.wrongDeleteContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkDatabaseObjectName(stmt);
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
        }
    }
}
