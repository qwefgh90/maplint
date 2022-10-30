package mybatis.diagnostics.mapperStatementDiagnostics;

import mybatis.diagnostics.MapperStatementDiagnostics;
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

public class CheckTypeExistenceTest {
    Logger logger = LoggerFactory.getLogger(CheckTypeExistenceTest.class);

    static Config config;
    Connection connection;

    @AfterEach
    void clean() throws SQLException {
        if (connection != null)
            connection.close();
    }

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, MyBatisProjectInitializationException {
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
    @DisplayName("Various forms")
    class VariousFormTest {
        @Test
        void test() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }


        @Test
        void test1() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form1");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 2);
        }
        @Test
        void test2() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form2");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test3() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form3");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test4() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form4");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test5() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form5");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test6() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form6");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 1);
        }
        @Test
        void test7() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form7");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
        @Test
        void test8() {
            var stmt = config.getMappedStatement("db.WrongPropertyName.form8");
            var diag = new MapperStatementDiagnostics();
            var result = diag.checkTypeExistence(stmt);
            logger.info(result.toString());
            Assertions.assertEquals(result.build().getErrorList().size(), 0);
        }
    }

//
//    @Nested
//    @DisplayName("Insert Statement")
//    class InsertStatementTest {
//        @Test
//        void test1() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.insert1");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 2);
//        }
//    }
//
//
//    @Nested
//    @DisplayName("Select Statement")
//    class SelectStatementTest {
//        @Test
//        void test1() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.select1");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 0);
//        }
//
//        @Test
//        void test2() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.select2");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 0);
//        }
//
//        @Test
//        void test3() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.select3");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 3);
//        }
//
//        @Test
//        void test4() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.select4");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 3);
//        }
//    }
//    @Nested
//    @DisplayName("Delete Statement")
//    class DeleteStatementTest {
//        @Test
//        void test1() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.delete1");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 0);
//        }
//        @Test
//        void test2() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.delete2");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 1);
//        }
//    }
//    @Nested
//    @DisplayName("Update Statement")
//    class UpdateStatementTest {
//        @Test
//        void test1() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.update1");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 0);
//        }
//        @Test
//        void test2() {
//            var stmt = config.getMappedStatement("db.WrongPropertyName.update2");
//            var diag = new MapperStatementDiagnostics();
//            var result = diag.checkTypeExistence(stmt);
//            logger.info(result.toString());
//            Assertions.assertEquals(result.build().getErrorList().size(), 2);
//        }
//    }
}
