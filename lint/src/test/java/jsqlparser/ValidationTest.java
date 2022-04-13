package jsqlparser;

import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.parser.sql.bound.DynamicBoundSqlStatementSource;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.feature.DatabaseType;
import net.sf.jsqlparser.util.validation.metadata.JdbcDatabaseMetaDataCapability;
import net.sf.jsqlparser.util.validation.metadata.NamesLookup;
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
import java.util.List;

public class ValidationTest {
    Logger logger = LoggerFactory.getLogger(ValidationTest.class);

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

    @Nested
    @DisplayName("with JDBCMetaData")
    class MetaDateTest {
        @Nested
        @DisplayName("Column name")
        class ColumnNameTest {
            @Test
            void selectWrongColumnName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithWrongColumnName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
                Assertions.assertEquals(1, errors.get(0).getErrors().size());
            }

            @Test
            void selectWrongTwoColumnNames() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithWrongTwoColumnNames").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
                Assertions.assertEquals(2, errors.get(0).getErrors().size());
            }

            @Test
            void insertWrongColumnName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.insertAuthorWithWrongColumnName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
                Assertions.assertEquals(1, errors.get(0).getErrors().size());
            }

            @Test
            void updateWrongColumnName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.updateContentWithWrongColumnName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
                Assertions.assertEquals(1, errors.get(0).getErrors().size());
            }

            @Test
            void selectWrongColumnNameInWhere() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithWrongColumnNameInWhere").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
                Assertions.assertEquals(2, errors.get(0).getErrors().size());
            }
        }

        @Nested
        @DisplayName("Table name")
        class TableName {
            @Test
            void selectWrongTableName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.insertAuthorWithWrongTableName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
            }

            @Test
            void wrongTableNameInJoin() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithWrongTableNameInJoin").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
            }

            @Test
            void updateWrongTableName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.updateContentWithWrongTableName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
            }
            @Test
            void insertWrongTableName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.insertContentWithWrongTableName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
            }
            @Test
            void deleteWrongTableName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
                var executableSql = config.getMappedStatement("db.WrongPosition.deleteContentWithWrongTableName").getBoundSql(new HashMap<>()).toString();
                logger.debug(executableSql);

                var env = config.getEnvironment();
                var manager = env.getTransactionManager();
                var transaction = manager
                        .getTransactionFactory()
                        .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
                var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
                connection = transaction.getConnection();

                Validation validation = new Validation(Arrays.asList(new JdbcDatabaseMetaDataCapability(connection,
                        NamesLookup.UPPERCASE)), executableSql);
                List<ValidationError> errors = validation.validate();
                logger.debug(errors.toString());
                Assertions.assertEquals(1, errors.size());
            }
        }
    }

    @Nested
    @DisplayName("Dynamic Bound Statement")
    class RawPlaceholderTest {
        @Test
        void dollarPlaceHolderSymbol() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            Assertions.assertInstanceOf(DynamicBoundSqlStatementSource.class, config.getMappedStatement("db.WrongPosition.selectContentByBlogWithDynamicBound").getSqlSource());
            Assertions.assertTrue(config.getMappedStatement("db.WrongPosition.selectContentByBlogWithDynamicBound").getSqlSource().isDynamic());
        }
    }

    @Nested
    @DisplayName("PlaceHolder Position")
    class PlaceholderTest {
        @Test
        void externalQuestionSymbol() {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithAmbiguousPlaceHolder").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            // external placeholder symbol is allowed to be anywhere
            Assertions.assertTrue(errors.size() == 0);
        }

        @Test
        void insertColumn() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.insertAuthorWithParameterAtAmbiguousPosition1").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            Assertions.assertEquals(1, errors.size());
        }

        @Test
        void tableName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.insertAuthorWithParameterAtAmbiguousPosition2").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            Assertions.assertEquals(1, errors.size());
        }

        @Test
        void selectColumnValue() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithAmbiguousPlaceHolder2").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            Assertions.assertTrue(errors.size() == 0);
        }

        @Test
        void insertConcat() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.insertAuthorWithParameterAtGoodPosition1").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            Assertions.assertTrue(errors.size() == 0);
        }
        @Test
        void selectConcat() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectBlogByAuthorWithConcat").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            Assertions.assertTrue(errors.size() == 0);
        }

        @Test
        void outside() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.insertAuthorWithParameterAtWrongPosition").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);

            Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
            List<ValidationError> errors = validation.validate();
            logger.debug(errors.toString());
            Assertions.assertTrue(errors.size() > 0);
        }
    }
}