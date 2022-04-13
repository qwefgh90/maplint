package jsqlparser;

import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;

public class ParseTest {
    Logger logger = LoggerFactory.getLogger(ParseTest.class);

    static Config config;

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root, "h2");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        config = parser.parse();
    }

    @Nested
    @DisplayName("Collect")
    class PlaceholderTest {
        @Test
        void e1() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithAmbiguousPlaceHolder2").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);
            var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
            var stmt = list.get(0);
            var select = (Select)stmt;
        }

        @Test
        void e2() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithAmbiguousPlaceHolder").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);
            var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
            var stmt = list.get(0);
            var select = (Select)stmt;
        }
        @Test
        void e3() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.BlogMapper.selectContentByBlog").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);
            var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
            var stmt = list.get(0);
            var select = (Select)stmt;
        }
    }
}