package mybatis.diagnostics.analysis;

import mybatis.diagnostics.analysis.jdbc.JDBCAnalysisService;
import mybatis.diagnostics.analysis.structure.StructureAnalysisService;
import mybatis.parser.XMLConfigParser;
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
import java.util.*;

public class ServiceTests {

    Logger logger = LoggerFactory.getLogger(ServiceTests.class);

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

//        var typeCollectCapability = new DatabaseMetadataCollectCapability(connection, NamesLookup.UPPERCASE);
//        Validation validation = new Validation(Arrays.asList(typeCollectCapability), executableSql);
//        List<ValidationError> errors = validation.validate();
//        logger.debug(typeCollectCapability.getColumnTypeMap().toString());
        var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
        logger.debug(meta.toString());

        var symbolSet = StructureAnalysisService.getSymbol((Select) StructureAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
    }

    @Test
    void select2() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.WrongBlogMapper.selectBlog2").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        connection = transaction.getConnection();

//        var typeCollectCapability = new DatabaseMetadataCollectCapability(connection, NamesLookup.UPPERCASE);
//        Validation validation = new Validation(Arrays.asList(typeCollectCapability), executableSql);
//        List<ValidationError> errors = validation.validate();
//        logger.debug(typeCollectCapability.getColumnTypeMap().toString());
        var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
        logger.debug(meta.toString());

        var symbolSet = StructureAnalysisService.getSymbol((Select) StructureAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
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

//        var databaseMetadataCollectCapability = new DatabaseMetadataCollectCapability(connection, NamesLookup.UPPERCASE);
//        Validation validation = new Validation(Arrays.asList(databaseMetadataCollectCapability), executableSql);
//        List<ValidationError> errors = validation.validate();
        var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
        logger.debug(meta.toString());

        var symbolSet = StructureAnalysisService.getSymbol((Insert) StructureAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
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

//        var namedObjectVisitor = new DatabaseMetadataCollectCapability(connection, NamesLookup.UPPERCASE);
//        Validation validation = new Validation(Arrays.asList(namedObjectVisitor), executableSql);
//        List<ValidationError> errors = validation.validate();
//        logger.debug(namedObjectVisitor.getColumnTypeMap().toString());
        var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
        logger.debug(meta.toString());

        var symbolSet = StructureAnalysisService.getSymbol((Update) StructureAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
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

//        var namedObjectVisitor = new DatabaseMetadataCollectCapability(connection, NamesLookup.UPPERCASE);
//        Validation validation = new Validation(Arrays.asList(namedObjectVisitor), executableSql);
//        List<ValidationError> errors = validation.validate();
//        logger.debug(namedObjectVisitor.getColumnTypeMap().toString());
        var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
        logger.debug(meta.toString());

        var symbolSet = StructureAnalysisService.getSymbol((Delete) StructureAnalysisService.parseStatement(executableSql));
        logger.debug(symbolSet.toString());
    }
}
