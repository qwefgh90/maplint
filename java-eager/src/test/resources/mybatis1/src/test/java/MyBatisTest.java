import com.mysql.cj.ClientPreparedQueryBindings;
import com.mysql.cj.QueryBindings;
import com.mysql.cj.Session;
import com.mysql.cj.xdevapi.SessionFactory;
import model.Author;
import model.Blog;
import model.Content;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyBatisTest {
    static Logger logger = LoggerFactory.getLogger(MyBatisTest.class);

    static SqlSession session;
    static Configuration conf;
    static Executor exec;
    static Transaction tran;
    static ParserUtils parserUtils;
    @BeforeAll
    static void before() {
        String resource = "config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        var s = new SessionFactory().getSession("");
//        new ClientPreparedQueryBindings(3, s);
//
//        new PreparedStatementHandler()
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);
        conf = parser.parse();
        var env = conf.getEnvironment();
        var transactionFactory = env.getTransactionFactory();
        tran = transactionFactory.newTransaction(env.getDataSource(), null, false);
        exec = conf.newExecutor(tran, ExecutorType.SIMPLE);
        parserUtils = new ParserUtils(conf, tran, exec);
        var mapped = conf.getMappedStatement("selectBlog");
        var a = mapped.getBoundSql("hello");
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream);
        session = sqlSessionFactory.openSession();
    }

    @AfterAll
    static void after() {
        session.close();
    }

    @Test
    @Order(1)
    void insertBlog() throws SQLException, JSQLParserException {
        //1. Run MySQL
        //  docker-compose -f stack.yml up -d
        //  localhost:8080
        var ms = conf.getMappedStatement("db.BlogMapper.insertBlog");
        var parseStatement = parserUtils.parse(ms, new Blog(0, "blogName", 1, "ccw", LocalDateTime.now()));
        logger.info("parsedStmt: " + parseStatement.toString());
        var key = session.insert("db.BlogMapper.insertBlog"
                , new Blog(0, "blogName", 1, "ccw", LocalDateTime.now()));
        logger.debug("insertBlog: " + key);
        logger.debug("\n\n");
    }

    @Test
    @Order(2)
    void selectBlog() throws SQLException, JSQLParserException {
        var ms = conf.getMappedStatement("db.BlogMapper.selectBlog");
        var parseStatement = parserUtils.parse(ms, "blogName");
        logger.info("parsedStmt: " + parseStatement.toString());
        Blog result = session.selectOne("db.BlogMapper.selectBlog", "blogName");
        logger.debug("selectBlog: " + result.toString());
        logger.debug("\n\n");
    }

    @Test
    @Order(2)
    void selectBlog2() throws SQLException, JSQLParserException {
        var ms = conf.getMappedStatement("db.BlogMapper.selectBlog2");
        var parseStatement = parserUtils.parse(ms, "FROM Blog");
        logger.info("parsedStmt: " + parseStatement.toString());
        Blog result = session.selectOne("db.BlogMapper.selectBlog2",
                "FROM Blog");
        logger.debug("selectBlog: " + result.toString());
        logger.debug("\n\n");
    }

    @Test
    @Order(3)
    void invalidSelectBlog() {
        assertThrows(Exception.class, () -> {
            try {
                session.selectOne("db.BlogMapper.invalidSelectBlog1"
                        , "myparam");
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug(e.toString());
                throw e;
            }
        });
    }

    @Test
    @Order(4)
    void invalidInsertBlog() {
        assertThrows(Exception.class, () -> {
            try {
                session.insert("db.BlogMapper.invalidInsertBlog1"
                        , new Blog(0, "blogName", 1, "ccw", LocalDateTime.now()));
            } catch (Exception e) {
                logger.debug(e.toString());
                throw e;
            }
        });
    }

    @Test
    void dynamicSelectBlog() throws SQLException, JSQLParserException {
        var ms = conf.getMappedStatement("db.BlogMapper.dynamicSelectBlog");
        var parseStatement = parserUtils.parse(ms, "blogName");
        logger.info("parsedStmt: " + parseStatement.toString());
        logger.debug("\n\n");
    }

    @Test
    void dynamicSelectContent() throws SQLException, JSQLParserException {
        var ms = conf.getMappedStatement("db.BlogMapper.dynamicSelectContent");
        var parseStatement = parserUtils.parse(ms,
                new Author("asdf"
                        , new Content("title", "content","chang")
                        , Collections.EMPTY_LIST));
        logger.info("parsedStmt: " + parseStatement.toString());
        logger.debug("\n\n");
    }

    @Test
    void dynamicSelectContent2() throws SQLException, JSQLParserException {
        var ms = conf.getMappedStatement("db.BlogMapper.dynamicSelectContent");
        var map = new HashMap<>();
        var map2 = new HashMap<>();
        map2.put("id", "hello");
        map.put("mainContent", map2);
        var parseStatement = parserUtils.parse(ms,
                map);
        logger.info("parsedStmt: " + parseStatement.toString());
        logger.debug("\n\n");
    }


    @Test
    void parseSelectContent() throws SQLException, JSQLParserException {
        var ms = conf.getMappedStatement("db.ContentMapper.selectContent");
        var parseStatement = parserUtils.parse(ms, new Author("name",  new Content("title", "content", "author"), Collections.EMPTY_LIST));
        logger.info("parsedStmt: " + parseStatement.toString());
        Blog result = session.selectOne("db.BlogMapper.selectContent", "blogName");
        logger.debug("selectContent: " + result.toString());
        logger.debug("\n\n");
    }
}
