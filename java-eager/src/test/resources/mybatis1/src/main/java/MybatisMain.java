import model.Author;
import model.Blog;
import model.Content;
import model.NaverBlog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import parsing.XPathParser;

import javax.xml.xpath.XPath;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisMain {
    static Logger logger = LoggerFactory.getLogger(MybatisMain.class);
    public static void main(String args[]) throws IOException {
        //1. Run MySQL
        //  docker-compose -f stack.yml up -d
        //  localhost:8080
        String resource = "config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream);
        var session = sqlSessionFactory.openSession();

        var key = session.insert("db.BlogMapper.insertBlog"
                , new Blog(0, "blogName", 1, "ccw", LocalDateTime.now()));
        logger.debug("insertBlog: " + key);
        logger.debug("\n\n");

        Blog result = session.selectOne("db.BlogMapper.selectBlog", "blogName");
        logger.debug("selectBlog: " + result.toString());
        logger.debug("\n\n");

        try {
            session.selectOne("db.BlogMapper.invalidSelectBlog1"
                    , "myparam");
        }catch(Exception e){
            logger.debug(e.toString());
            logger.debug("\n\n");
        }

        try {
            session.insert("db.BlogMapper.invalidInsertBlog1"
                    , new Blog(0, "blogName", 1, "ccw", LocalDateTime.now()));
        }catch(Exception e){
            e.printStackTrace();
            logger.debug(e.toString());
            logger.debug("\n\n");
        }

        //2. Select Parameter Notation 분석
//        HashMap<String, String> blog = session.selectOne(
//                "db.BlogMapper.testSelectBlog", "name");
//        logger.debug("db.BlogMapper.testSelectBlog");
//        logger.debug(blog.toString());
//
//        //3. ResultMap 분석
//        NaverBlog nblog = session.selectOne(
//                "db.BlogMapper.selectBlog", new Blog("","",new Date(), 400));
//        logger.debug("db.BlogMapper.selectBlog");
//        logger.debug(nblog.toString());
//
//        //4. Primitive/String Type Parameter 분석
//        var content = new Content("타이틀", "내용", "최창원");
//        var id = session.insert("db.BlogMapper.insertContent", content);
//        logger.debug("db.BlogMapper.insertContent");
//        logger.debug(content.toString());
//        logger.debug(String.valueOf(id));
//        session.commit();
//
//        Author author = session.selectOne(
//                "db.BlogMapper.selectAuthor");
//        logger.debug("db.BlogMapper.selectAuthor");
//        logger.debug(author.toString());

        session.close();
//=======
//        Blog blog = session.selectOne(
//                "db.BlogMapper.selectBlog");
//        System.out.println(blog.toString());
//        System.out.println("Hello World");
//
//        inputStream = Resources.getResourceAsStream(resource);
//        XPathParser parser = new XPathParser(inputStream, true, null, new XMLMapperEntityResolver());
//        var node = parser.evalNode("/configuration/environments");
//        var value = node.getStringAttribute("default");
//        System.out.println(value);
//>>>>>>> 파서추가
    }
}

//### Cause: org.apache.ibatis.builder.BuilderException: Error parsing SQL Mapper Configuration. Cause: org.apache.ibatis.builder.BuilderException: Error parsing Mapper XML. The XML location is 'db/BlogMapper.xml'. Cause: org.apache.ibatis.builder.BuilderException: Error resolving class. Cause: org.apache.ibatis.type.TypeException: Could not resolve type alias 'Blog'.  Cause: java.lang.ClassNotFoundException: Cannot find class: Blog
//        at org.apache.ibatis.exceptions.ExceptionFactory.wrapException(ExceptionFactory.java:30)
//        at org.apache.ibatis.session.SqlSessionFactoryBuilder.build(SqlSessionFactoryBuilder.java:80)
//        at org.apache.ibatis.session.SqlSessionFactoryBuilder.build(SqlSessionFactoryBuilder.java:64)
//        at MybatisMain.main(MybatisMain.java:13)