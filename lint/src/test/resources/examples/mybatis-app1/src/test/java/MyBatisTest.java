import model.Author;
import model.Blog;
import model.Content;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * !Important
 * All tests must not commit changes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyBatisTest {
    private static Logger logger = LoggerFactory.getLogger(MyBatisTest.class);

    @BeforeAll
    static void newSession() throws IOException, SQLException {
        String resource = "config-test.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream);
        var session = sqlSessionFactory.openSession();
        MyBatisTest.session = session;

        session.update("db.BlogMapper.createTableIfNotExist");
        logger.info(String.format("Tables have been created."));
    }

    static SqlSession session;
    static Author author;
    static Blog blog;
    static Content content;
    static Content content2;

    @Test
    void printPreparedStatement() throws IOException, SQLException {
        String resource = "config-test.xml";
        var inputStream = Resources.getResourceAsStream(resource);
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);
        var configuration = parser.parse();
        var env = configuration.getEnvironment();
        var transactionFactory = env.getTransactionFactory();
        var tran = transactionFactory.newTransaction(env.getDataSource(), null, false);
        var exec = configuration.newExecutor(tran, ExecutorType.SIMPLE);

        StatementHandler handler = configuration.newStatementHandler(exec,
                configuration.getMappedStatement("db.BlogMapper.insertAuthor"),
                new Author(0, "창원"), RowBounds.DEFAULT, null, null);

        var connection = tran.getConnection();
        var stmt = handler.prepare(connection, tran.getTimeout());
        handler.parameterize(stmt);
        var stmtToString = stmt.toString();
        logger.info(stmtToString.substring(stmtToString.indexOf(':') + 1).trim());
    }

    @Test
    void test() {
        var authors = session.selectList("db.BlogMapper.test", "Author");
        for (var a : authors) {
            logger.info("A inserted row: {}", a.toString());
        }
    }
    @Test
    void test2() {
        var authors = session.selectList("db.BlogMapper.getReport", Map.of("year","1234", "month", "OCT"));
        for (var a : authors) {
            logger.info("A inserted row: {}", a.toString());
        }
    }
    @Test
    void getLog() {

        var count = session.selectList("db.BlogMapper.getLog", "tent");
        logger.info("list: {}", count);
    }

    @Test
    @Order(1)
    void makeNewAuthor() {
        author = new Author(0, "창원");
        var count = session.insert("db.BlogMapper.insertAuthor", author);
        logger.info("{} where a new key must be populated.", author.toString());
        var authors = session.selectList("db.BlogMapper.selectAuthor");
        for (var a : authors) {
            logger.info("A inserted row: {}", a.toString());
        }
    }

    @Test
    @Order(2)
    void makeBlog() {
        blog = new Blog(0, author, "나의 일상 블로그"
                , "여행, 음식, 일상을 공유하는 블로그입니다.", LocalDateTime.now(), List.of());
        var count = session.insert("db.BlogMapper.insertBlog", blog);
        logger.info("{} where a new key must be populated.", blog.toString());
        var blogs = session.selectList("db.BlogMapper.selectBlogByAuthor"
                , author.secretlyGetId());
        for (var a : blogs) {
            logger.info("A inserted row: {}", a.toString());
        }
    }

    @Test
    @Order(3)
    void makeContent() {
        content = new Content(0, author, blog.secretlyGetId(), "첫번째 블로그 포스트", LocalDateTime.now());
        content2 = new Content(0, author, blog.secretlyGetId(), "두번째 블로그 포스트", LocalDateTime.now());
        session.insert("db.BlogMapper.insertContent", content);
        session.insert("db.BlogMapper.insertContent", content2);
        logger.info("{} where a new key must be populated.", content.toString());
        logger.info("{} where a new key must be populated.", content2.toString());
        var contents = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
                , blog.secretlyGetId());
        for (var a : contents) {
            logger.info("A inserted row: {}", a.toString());
        }
        Assertions.assertEquals(contents.size(), 2);
    }

    @Test
    @Order(4)
    void selectBlogWithContents() {
        var blogs = session.selectList("db.BlogMapper.selectBlogByAuthor"
                , author.secretlyGetId());
        for (var a : blogs) {
            logger.info("A blog with contents: {}", a.toString());
            for (var c: ((Blog)a).secretlyGetContents()){
                logger.info("A content: {}", c.toString());
            }
        }
    }

    @Test
    @Order(5)
    void updateAndDeleteContent() {
        var contents = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
                , blog.secretlyGetId());
        Assertions.assertEquals(contents.size(), 2);
        //Update
        for (var a : contents) {
            a.setContent("비어있는 포스트");
            session.update("db.BlogMapper.updateContent", a);
        }
        var contentsAfterUpdate = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
                , blog.secretlyGetId());
        for (var a : contentsAfterUpdate) {
            logger.info("A updated row: {}", a.toString());
            Assertions.assertEquals(a.getContent(), "비어있는 포스트");
        }
        //Delete
        for (var a : contentsAfterUpdate) {
            session.delete("db.BlogMapper.deleteContent", a.getId());
        }
        var contentsAfterRemove = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
                , blog.secretlyGetId());
        Assertions.assertEquals(contentsAfterRemove.size(), 0);
    }
}
