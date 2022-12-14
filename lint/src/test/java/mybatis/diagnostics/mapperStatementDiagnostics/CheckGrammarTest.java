package mybatis.diagnostics.mapperStatementDiagnostics;

import mybatis.diagnostics.MapperStatementDiagnostics;
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

public class CheckGrammarTest {
    static Logger logger = LoggerFactory.getLogger(CheckGrammarTest.class);

    static Config config;
    Connection connection;

    static int STP = 0;
    static int SFP = 0;
    static int STN = 0;
    static int SFN = 0;

    @AfterEach
    void clean() throws SQLException {
        if(connection!= null)
            connection.close();
    }

    @AfterAll
    static void statistics(){
        logger.info("TP: {}, FP: {}, TN: {}, FN: {}", STP, SFP, STN, SFN);
    }

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var ddl = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1/src/main/resources/db/Tables.ddl").toURI()).normalize();
        var server = new MyBatisProjectService(root, "h2");
        config = server.getParsedConfig();
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
    class InsertStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.InsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());

            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }
        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }


        @Test
        void testP2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }

        @Test
        void testP3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }

        @Test
        void testP4() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }

    @Nested
    @DisplayName("Insert Select Statement")
    class InsertSelectStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.InsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }

        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }

        @Test
        void testP2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }

        @Test
        void testP3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }

        @Test
        void testP4() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }

        @Test
        void testP5() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }

    @Nested
    @DisplayName("Select Join Where Statement")
    class SelectStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.SelectJoinStatement");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }
        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP4() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP5() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP6() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement6");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }

    @Nested
    @DisplayName("Select Union Statement")
    class SelectUnionStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.SelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }
        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }
    @Nested
    @DisplayName("Select Into Statement")
    class SelectIntoStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.SelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }
        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }
    @Nested
    @DisplayName("Update Statement")
    class UpdateStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.updateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }
        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongUpdateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongUpdateContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongUpdateContent3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }

    @Nested
    @DisplayName("Delete Statement")
    class DeleteStatementTest {
        @Test
        void testN1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.deleteContent");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
            logger.info("TP: {}, FP: {}", 0, 0);
            logger.info("TN: {}, FN: {}", 1, 0);
            STN++;
        }
        @Test
        void testP1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongDeleteContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
        @Test
        void testP2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongDeleteContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info("TP: {}, FP: {}", 1, 0);
            logger.info("TN: {}, FN: {}", 0, 0);
            STP++;

        }
    }
}
