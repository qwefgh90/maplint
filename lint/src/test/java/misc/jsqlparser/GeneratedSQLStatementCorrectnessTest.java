package misc.jsqlparser;

import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.feature.DatabaseType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GeneratedSQLStatementCorrectnessTest {
    Logger logger = LoggerFactory.getLogger(GeneratedSQLStatementCorrectnessTest.class);

    static Config config;

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService(root, "h2");
        config = server.getParsedConfig();
    }

    @Test
    void statement1() throws ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertAuthor").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement2() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement3() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectAuthor").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement4() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement5() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectBlogByAuthor").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement6() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectContentByBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement7() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement8() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.deleteContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }

    @Test
    void statement9() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.updateContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
}