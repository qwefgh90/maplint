package lint.cli;

import lint.cli.option.LintOption;
import mybatis.diagnostics.exception.DatabaseObjectNameCheckException;
import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import org.apache.ibatis.session.ExecutorType;
import org.junit.jupiter.api.AfterEach;
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
import java.util.HashMap;

/**
 * @author qwefgh90
 */
public class LintServiceTest {
    Logger logger = LoggerFactory.getLogger(LintServiceTest.class);

    static void setupMyBatisApp() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, DatabaseObjectNameCheckException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root, "h2");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        var config = parser.parse();
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

    static void setupLoginProject() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, DatabaseObjectNameCheckException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/login-project").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root, "h2");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        var config = parser.parse();
        var env = config.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        var exec = config.newExecutor(transaction, ExecutorType.SIMPLE);
        var connection = transaction.getConnection();
        var mapper = config.getMappedStatement("db.LoginMapper.createTable");
        var pstmt = connection.prepareStatement(mapper.getSqlSource().getBoundSql(new HashMap()).toString());
        pstmt.execute();
        connection.close();
    }
    @Test
    void lint1() throws URISyntaxException, SQLException, ConfigNotFoundException, DatabaseObjectNameCheckException, IOException, MyBatisProjectInitializationException {
        setupMyBatisApp();
        LintService lintService = new LintService();
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var report = lintService.lint(LintOption.LintOptionBuilder.aLintOption(root).configFileName("h2").build());
        logger.info(report.toString());
    }

    @Test
    void lint2() throws URISyntaxException, SQLException, ConfigNotFoundException, DatabaseObjectNameCheckException, IOException, MyBatisProjectInitializationException {
        setupLoginProject();
        LintService lintService = new LintService();
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/login-project").toURI()).normalize();
        var report = lintService.lint(LintOption.LintOptionBuilder.aLintOption(root).configFileName("h2").build());
        logger.info(report.toString());
    }
}
