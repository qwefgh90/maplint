import model.SnsUser;
import model.User;
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

/**
 * !Important
 * All tests must not commit changes.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyBatisTest {
    private static Logger logger = LoggerFactory.getLogger(MyBatisTest.class);

    User user;
    SnsUser snsUser;

    @BeforeAll
    static void newSession() throws IOException, SQLException {
        String resource = "config-test.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream);
        var session = sqlSessionFactory.openSession();
        MyBatisTest.session = session;

        session.update("db.LoginMapper.createTable");
        logger.info(String.format("Tables have been created."));
    }

    static SqlSession session;

    @Test
    @Order(1)
    void makeNewUser() {
        user = new User("John", "Password", "Email", "Frusci", "0101111");
        var count = session.insert("db.LoginMapper.insertNormalUser", user);
        logger.info("{} where a new key must be populated.", user.toString());
        var users = session.selectList("db.LoginMapper.selectUserById", user.getId());
        for (var a : users) {
            logger.info("A inserted row: {}", a.toString());
        }
    }

    @Test
    @Order(2)
    void makeNewUserFromSNS() {
        user = new User("Charlie", "Password", "Email", x "Flea", "010111111");
        session.insert("db.LoginMapper.insertNormalUser", user);
        snsUser = new SnsUser(user.getId(), "123", "Normal", "SnsName", "SnsProfile");
        session.insert("db.LoginMapper.insertSnsUser", snsUser);
        logger.info("{} where a new key must be populated.", user.toString());
        logger.info("{} where a new key must be populated.", snsUser.toString());
        var users = session.selectList("db.LoginMapper.selectSnsUserBySnsId", "123");
        for (var a : users) {
            logger.info("A inserted row: {}", a.toString());
        }
    }

//
//    @Test
//    @Order(2)
//    void makeBlog() {
//        blog = new Blog(0, author, "나의 일상 블로그"
//                , "여행, 음식, 일상을 공유하는 블로그입니다.", LocalDateTime.now(), List.of());
//        var count = session.insert("db.BlogMapper.insertBlog", blog);
//        logger.info("{} where a new key must be populated.", blog.toString());
//        var blogs = session.selectList("db.BlogMapper.selectBlogByAuthor"
//                , author.secretlyGetId());
//        for (var a : blogs) {
//            logger.info("A inserted row: {}", a.toString());
//        }
//    }
//
//    @Test
//    @Order(3)
//    void makeContent() {
//        content = new Content(0, author, blog.secretlyGetId(), "첫번째 블로그 포스트", LocalDateTime.now());
//        content2 = new Content(0, author, blog.secretlyGetId(), "두번째 블로그 포스트", LocalDateTime.now());
//        session.insert("db.BlogMapper.insertContent", content);
//        session.insert("db.BlogMapper.insertContent", content2);
//        logger.info("{} where a new key must be populated.", content.toString());
//        logger.info("{} where a new key must be populated.", content2.toString());
//        var contents = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
//                , blog.secretlyGetId());
//        for (var a : contents) {
//            logger.info("A inserted row: {}", a.toString());
//        }
//        Assertions.assertEquals(contents.size(), 2);
//    }
//
//    @Test
//    @Order(4)
//    void selectBlogWithContents() {
//        var blogs = session.selectList("db.BlogMapper.selectBlogByAuthor"
//                , author.secretlyGetId());
//        for (var a : blogs) {
//            logger.info("A blog with contents: {}", a.toString());
//            for (var c: ((Blog)a).secretlyGetContents()){
//                logger.info("A content: {}", c.toString());
//            }
//        }
//    }
//
//    @Test
//    @Order(5)
//    void updateAndDeleteContent() {
//        var contents = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
//                , blog.secretlyGetId());
//        Assertions.assertEquals(contents.size(), 2);
//        //Update
//        for (var a : contents) {
//            a.setContent("비어있는 포스트");
//            session.update("db.BlogMapper.updateContent", a);
//        }
//        var contentsAfterUpdate = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
//                , blog.secretlyGetId());
//        for (var a : contentsAfterUpdate) {
//            logger.info("A updated row: {}", a.toString());
//            Assertions.assertEquals(a.getContent(), "비어있는 포스트");
//        }
//        //Delete
//        for (var a : contentsAfterUpdate) {
//            session.delete("db.BlogMapper.deleteContent", a.getId());
//        }
//        var contentsAfterRemove = session.<Content>selectList("db.BlogMapper.selectContentByBlog"
//                , blog.secretlyGetId());
//        Assertions.assertEquals(contentsAfterRemove.size(), 0);
//    }
}
