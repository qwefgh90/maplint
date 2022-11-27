package sql.analysis;

import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.feature.FeatureConfiguration;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.validation.*;
import net.sf.jsqlparser.util.validation.metadata.Named;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;
import org.apache.ibatis.session.ExecutorType;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.analysis.tree.model.DeleteStructuralData;
import sql.analysis.tree.model.InsertStructuralData;
import sql.analysis.tree.model.SelectStructuralData;
import sql.analysis.tree.model.UpdateStructuralData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class SQLAnalysisServiceTest {

    Logger logger = LoggerFactory.getLogger(SQLAnalysisServiceTest.class);

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

    @Test
    void select() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.SampleMapper.selectBlogByAuthor2").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        connection = transaction.getConnection();

        var meta = SQLAnalysisService.analyze(connection, executableSql);
        logger.debug(meta.toString());
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Blog").setAlias("B")).get(0));
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Author").setAlias("A")).get(0));
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Content").setAlias("C")).get(0));
        Assertions.assertTrue(meta.getColumnExistMap().entrySet().stream()
                .filter(e -> e.getKey().getNamedObject().equals(NamedObject.column))
                .count() >= 2);

        var symbolSet = SQLAnalysisService.getSymbolSet((Select) SQLAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
        Assertions.assertTrue(symbolSet instanceof SelectStructuralData);
    }

    @Test
    void wrongObjectName() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.WrongBlogMapper.selectBlog2").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        connection = transaction.getConnection();

        var meta = SQLAnalysisService.analyze(connection, executableSql);
        logger.debug(meta.toString());
        Assertions.assertFalse(meta.exists(new Named(NamedObject.table, "Blog").setAlias("B")).get(0));
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Author").setAlias("A")).get(0));
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Content").setAlias("C")).get(0));
        Assertions.assertTrue(meta.getColumnExistMap().entrySet().stream()
                .filter(e -> e.getKey().getNamedObject().equals(NamedObject.column))
                .count() >= 2);

        var symbolSet = SQLAnalysisService.getSymbolSet((Select) SQLAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
        Assertions.assertTrue(symbolSet instanceof SelectStructuralData);
    }

    public static ValidationContext createValidationContext() {
        ValidationContext context = new ValidationContext();
        context.setCapabilities(new ArrayList<>());
        context.setConfiguration(new FeatureConfiguration());
        return context;
    }

    @Test
    void insert() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        connection = transaction.getConnection();

        var meta = SQLAnalysisService.analyze(connection, executableSql);
        logger.debug(meta.toString());
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Blog")).get(0));
        Assertions.assertTrue(meta.getColumnExistMap().entrySet().stream()
                        .filter(e -> e.getKey().getNamedObject().equals(NamedObject.column))
                        .count() >= 4);

        var symbolSet = SQLAnalysisService.getSymbolSet((Insert) SQLAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
        Assertions.assertTrue(symbolSet instanceof InsertStructuralData);
    }

    @Test
    void update() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.updateContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        connection = transaction.getConnection();
        var meta = SQLAnalysisService.analyze(connection, executableSql);
        logger.debug(meta.toString());
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Content")).get(0));
        Assertions.assertTrue(meta.getColumnExistMap().entrySet().stream()
                .filter(e -> e.getKey().getNamedObject().equals(NamedObject.column))
                .count() >= 1);

        var symbolSet = SQLAnalysisService.getSymbolSet((Update) SQLAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
        Assertions.assertTrue(symbolSet instanceof UpdateStructuralData);
    }

    @Test
    void delete() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.deleteContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        connection = transaction.getConnection();

        var meta = SQLAnalysisService.analyze(connection, executableSql);
        logger.debug(meta.toString());
        Assertions.assertTrue(meta.exists(new Named(NamedObject.table, "Content")).get(0));
        Assertions.assertTrue(meta.exists(new Named(NamedObject.column, "id")).get(0));

        var symbolSet = SQLAnalysisService.getSymbolSet((Delete) SQLAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
        Assertions.assertTrue(symbolSet instanceof DeleteStructuralData);
    }
}
