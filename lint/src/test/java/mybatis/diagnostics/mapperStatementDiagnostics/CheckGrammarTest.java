package mybatis.diagnostics.mapperStatementDiagnostics;

import mybatis.diagnostics.MapperStatementDiagnostics;
import mybatis.diagnostics.analysis.structure.visitor.DefaultContextProvider;
import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import mybatis.util.MapperUtil;
import net.sf.jsqlparser.util.validation.Validation;
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
import java.util.Arrays;
import java.util.HashMap;

public class CheckGrammarTest {
    Logger logger = LoggerFactory.getLogger(CheckGrammarTest.class);

    static Config config;
    Connection connection;

    @AfterEach
    void clean() throws SQLException {
        if(connection!= null)
            connection.close();
    }

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
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
        var mapper = config.getMappedStatement("db.BlogMapper.createTableIfNotExist");
        var pstmt = connection.prepareStatement(mapper.getSqlSource().getBoundSql(new HashMap()).toString());
        pstmt.execute();
        connection.close();
    }

    void basicProcedure(){
        var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement1");
        var executableSqlWithSignature = stmt.getBoundSql(new HashMap<>()).toString();
        var executableSql = MapperUtil.trimSignature(executableSqlWithSignature);
        var val = new Validation(Arrays.asList(DefaultContextProvider.DummyCapability.getInstance()), executableSql);
        var r = val.validate();
        logger.info(r.toString());
    }

    @Nested
    @DisplayName("Insert Into Statement")
    class InsertStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.InsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }
        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }


        @Test
        void test2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }

        @Test
        void test4() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }

        @Test
        void test5() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertStatement5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }

    @Nested
    @DisplayName("Insert Select Statement")
    class InsertSelectStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.InsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }

        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }

        @Test
        void test2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }

        @Test
        void test3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }

        @Test
        void test4() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }

        @Test
        void test5() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongInsertSelectStatement5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }

    @Nested
    @DisplayName("Select Join Where Statement")
    class SelectStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.SelectJoinStatement");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }
        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test4() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test5() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test6() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectStatement6");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }

    @Nested
    @DisplayName("Select Union Statement")
    class SelectUnionStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.SelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }
        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectUnionStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }
    @Nested
    @DisplayName("Select Into Statement")
    class SelectIntoStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.SelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }
        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.WrongSelectIntoStatement1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }
    @Nested
    @DisplayName("Update Statement")
    class UpdateStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.updateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }
        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongUpdateContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongUpdateContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test3() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongUpdateContent3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }

    @Nested
    @DisplayName("Delete Statement")
    class DeleteStatementTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongGrammar.deleteContent");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().isEmpty());
        }
        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongDeleteContent1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
        @Test
        void test2() {
            var stmt = config.getMappedStatement("db.WrongGrammar.wrongDeleteContent2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkGrammar(stmt);
            Assertions.assertTrue(result.build().getErrorList().size() > 0);
            logger.info(result.build().getErrorList().get(0).toString());
        }
    }
}
