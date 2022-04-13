package jsqlparser.h2;

import mybatis.parser.XMLConfigParser;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ValidationTestWithGoodTemplates {
    Logger logger = LoggerFactory.getLogger(ValidationTestWithGoodTemplates.class);

    static Config config;

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root, "h2");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        config = parser.parse();
    }

    @Test
    void validation1() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertAuthor").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation2() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation3() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectAuthor").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation4() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation5() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectBlogByAuthor").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation6() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.selectContentByBlog").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation7() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.insertContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation8() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.deleteContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation9() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.updateContent").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation10() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.createTableIfNotExist").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
    @Test
    void validation11() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
        var executableSql = config.getMappedStatement("db.BlogMapper.createTableIfNotExistSQLite").getBoundSql(new HashMap<>()).toString();
        logger.debug(executableSql);

        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), executableSql);
        List<ValidationError> errors = validation.validate();
        logger.debug(errors.toString());
        Assertions.assertTrue(errors.size() == 0);
    }
}