package mybatis.parser;

import els.type.ClassType;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
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
import java.util.stream.Collectors;

public class XMLConfigParserTest {
    Logger logger = LoggerFactory.getLogger(XMLConfigParserTest.class);

    static Config config;
    Connection connection;

    @AfterEach
    void clean() throws SQLException {
        if(connection!= null)
            connection.close();
    }

    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root);
        var path = server.getConfigFile();
        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        config = parser.parse();
    }

    @Nested
    @DisplayName("Insert Statement")
    class InsertStatementTest {
        @Test
        void insertAuthorTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.insertAuthor");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.insertAuthor");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "model.Author");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 1);
            var mapping = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping.getJavaType(), "java.lang.String");
            Assertions.assertEquals(stmt.getResultMaps().size(), 0);
        }

        @Test
        void insertBlogTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.insertBlog");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.insertBlog");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "model.Blog");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 4);
            var mapping1 = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "author.id");
            Assertions.assertEquals(mapping1.getJavaType(), "int");
            var mapping2 = stmt.getSqlSource().getParameterMappings().get(1);
            Assertions.assertEquals(mapping2.getProperty(), "blogName");
            Assertions.assertEquals(mapping2.getJavaType(), "java.lang.String");
            var mapping3 = stmt.getSqlSource().getParameterMappings().get(2);
            Assertions.assertEquals(mapping3.getProperty(), "description");
            Assertions.assertEquals(mapping3.getJavaType(), "java.lang.String");
            var mapping4 = stmt.getSqlSource().getParameterMappings().get(3);
            Assertions.assertEquals(mapping4.getProperty(), "createTime");
            Assertions.assertEquals(mapping4.getJavaType(), "java.time.LocalDateTime");
            Assertions.assertEquals(stmt.getResultMaps().size(), 0);
        }

        @Test
        void insertContentTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.insertContent");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.insertContent");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "model.Content");
            Assertions.assertEquals(stmt.getKeyProperties().length, 1);
            Assertions.assertEquals(stmt.getKeyProperties()[0], "id");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 4);
            var mapping1 = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "author.id");
            Assertions.assertEquals(mapping1.getJavaType(), "int");
            var mapping2 = stmt.getSqlSource().getParameterMappings().get(1);
            Assertions.assertEquals(mapping2.getProperty(), "blogId");
            Assertions.assertEquals(mapping2.getJavaType(), "int");
            var mapping3 = stmt.getSqlSource().getParameterMappings().get(2);
            Assertions.assertEquals(mapping3.getProperty(), "content");
            Assertions.assertEquals(mapping3.getJavaType(), "java.lang.String");
            var mapping4 = stmt.getSqlSource().getParameterMappings().get(3);
            Assertions.assertEquals(mapping4.getProperty(), "createTime");
            Assertions.assertEquals(mapping4.getJavaType(), "java.time.LocalDateTime");
        }
    }

    @Nested
    @DisplayName("Select Statement")
    class SelectStatementTest {
        @Test
        void selectAuthorTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.selectAuthor");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.selectAuthor");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 0);
            Assertions.assertEquals(stmt.getResultMaps().size(), 1);
            var map = stmt.getResultMaps().get(0);
            Assertions.assertEquals(map.getType(), "model.Author");
        }

        @Test
        void selectBlogTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.selectBlog");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.selectBlog");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 0);
            Assertions.assertEquals(stmt.getResultMaps().size(), 1);
            var map = stmt.getResultMaps().get(0);
            Assertions.assertEquals(map.getType(), "model.Blog");
            Assertions.assertEquals(map.getId(), "db.BlogMapper.blogResult");
            var mappings = map.getResultMappings();
            var mapping = mappings.get(0);
            Assertions.assertEquals(mapping.getProperty(), "id");
            Assertions.assertEquals(mapping.getColumn(), "id");
            Assertions.assertEquals(mapping.getJavaType(), "int");
            mapping = mappings.get(1);
            Assertions.assertEquals(mapping.getProperty(), "blogName");
            Assertions.assertEquals(mapping.getColumn(), "blog_name");
            Assertions.assertEquals(mapping.getJavaType(), "java.lang.String");
            mapping = mappings.get(2);
            Assertions.assertEquals(mapping.getProperty(), "description");
            Assertions.assertEquals(mapping.getColumn(), "description");
            Assertions.assertEquals(mapping.getJavaType(), "java.lang.String");
            mapping = mappings.get(3);
            Assertions.assertEquals(mapping.getProperty(), "createTime");
            Assertions.assertEquals(mapping.getColumn(), "create_time");
            Assertions.assertEquals(mapping.getJavaType(), "java.time.LocalDateTime");
            mapping = mappings.get(4);
            Assertions.assertEquals(mapping.getProperty(), "author");
            Assertions.assertEquals(mapping.getJavaType(), "model.Author");
            Assertions.assertEquals(mapping.getNestedResultMapId(), "db.BlogMapper.authorResult");
            Assertions.assertEquals(mapping.getColumnPrefix(), "author_");
            mapping = mappings.get(5);
            Assertions.assertEquals(mapping.getProperty(), "contents");
            Assertions.assertEquals(mapping.getJavaType(), "java.util.List<model.Content>");
            Assertions.assertEquals(mapping.getNestedResultMapId(), "db.BlogMapper.contentResult");
            Assertions.assertEquals(mapping.getColumnPrefix(), "content_");
        }

        @Test
        void selectBlogByAuthorTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.selectBlogByAuthor");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.selectBlogByAuthor");
            Assertions.assertNotNull(stmt.getParameterMap());
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 1);
            var param = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(param.getJavaType(), "java.lang.Integer");
            Assertions.assertEquals(param.getProperty(), "id");
            Assertions.assertEquals(stmt.getResultMaps().size(), 1);
            var rm = stmt.getResultMaps().get(0);
            Assertions.assertEquals(rm.getId(), "db.BlogMapper.blogResult");
            Assertions.assertEquals(rm.getType(), "model.Blog");
            Assertions.assertEquals(rm.getResultMappings().size(), 6);
            var result1 = rm.getResultMappings().get(0);
            Assertions.assertEquals(result1.getProperty(), "id");
            Assertions.assertEquals(result1.getColumn(), "id");
            var result2 = rm.getResultMappings().get(1);
            Assertions.assertEquals(result2.getProperty(), "blogName");
            Assertions.assertEquals(result2.getColumn(), "blog_name");
            var result3 = rm.getResultMappings().get(2);
            Assertions.assertEquals(result3.getProperty(), "description");
            Assertions.assertEquals(result3.getColumn(), "description");
            var result4 = rm.getResultMappings().get(3);
            Assertions.assertEquals(result4.getProperty(), "createTime");
            Assertions.assertEquals(result4.getColumn(), "create_time");
            var result5 = rm.getResultMappings().get(4);
            Assertions.assertEquals(result5.getProperty(), "author");
            Assertions.assertEquals(result5.getColumnPrefix(), "author_");
            Assertions.assertEquals(result5.getNestedResultMapId(), "db.BlogMapper.authorResult");
            var result6 = rm.getResultMappings().get(5);
            Assertions.assertEquals(result6.getProperty(), "contents");
            Assertions.assertEquals(result6.getColumnPrefix(), "content_");
            Assertions.assertEquals(result6.getNestedResultMapId(), "db.BlogMapper.contentResult");
        }

        @Test
        void selectContentByBlogTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.selectContentByBlog");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.selectContentByBlog");
            Assertions.assertNotNull(stmt.getParameterMap());
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 1);
            var param = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(param.getJavaType(), "java.lang.Integer");
            Assertions.assertEquals(param.getProperty(), "id");
            Assertions.assertEquals(stmt.getResultMaps().size(), 1);
            var rm = stmt.getResultMaps().get(0);
            Assertions.assertEquals(rm.getId(), "db.BlogMapper.contentResult");
            Assertions.assertEquals(rm.getType(), "model.Content");
            Assertions.assertEquals(rm.getResultMappings().size(), 5);
            var result1 = rm.getResultMappings().get(0);
            Assertions.assertEquals(result1.getProperty(), "id");
            Assertions.assertEquals(result1.getColumn(), "id");
            Assertions.assertEquals(result1.getJavaType(), "int");
            var result2 = rm.getResultMappings().get(1);
            Assertions.assertEquals(result2.getProperty(), "content");
            Assertions.assertEquals(result2.getColumn(), "content");
            Assertions.assertEquals(result2.getJavaType(), "java.lang.String");
            var result3 = rm.getResultMappings().get(2);
            Assertions.assertEquals(result3.getProperty(), "blogId");
            Assertions.assertEquals(result3.getColumn(), "blog_id");
            Assertions.assertEquals(result3.getJavaType(), "int");
            var result4 = rm.getResultMappings().get(3);
            Assertions.assertEquals(result4.getProperty(), "createTime");
            Assertions.assertEquals(result4.getColumn(), "create_time");
            Assertions.assertEquals(result4.getJavaType(), "java.time.LocalDateTime");
            var result5 = rm.getResultMappings().get(4);
            Assertions.assertEquals(result5.getProperty(), "author");
            Assertions.assertEquals(result5.getColumnPrefix(), "author_");
            Assertions.assertEquals(result5.getNestedResultMapId(), "db.BlogMapper.authorResult");
        }
    }

    @Nested
    @DisplayName("Delete and Update Statement")
    class DeleteUpdateStatementTest {
        @Test
        void deleteContentTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.deleteContent");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.deleteContent");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "int");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 1);
            var mapping1 = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "id");
            Assertions.assertEquals(mapping1.getJavaType(), "java.lang.Integer");
        }

        @Test
        void updateContentTest() {
            var stmt = config.getMappedStatement("db.BlogMapper.updateContent");
            Assertions.assertEquals(stmt.getId(), "db.BlogMapper.updateContent");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "model.Content");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 2);
            var mapping1 = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "content");
            Assertions.assertEquals(mapping1.getJavaType(), "java.lang.String");
            var mapping2 = stmt.getSqlSource().getParameterMappings().get(1);
            Assertions.assertEquals(mapping2.getProperty(), "id");
            Assertions.assertEquals(mapping2.getJavaType(), "int");
        }
    }

    @Nested
    @DisplayName("Unexpected uses")
    class UnexpectedUseTest {
        @Test
        void wrongParameterPropertyNameTest() {
            var stmt = config.getMappedStatement("db.WrongBlogMapper.insertBlog");
            Assertions.assertEquals(stmt.getId(), "db.WrongBlogMapper.insertBlog");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "model.Blog");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 4);
            var mapping1 = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "author.id1");
            Assertions.assertEquals(mapping1.getJavaType(), "java.lang.Object");
            var mapping2 = stmt.getSqlSource().getParameterMappings().get(1);
            Assertions.assertEquals(mapping2.getProperty(), "blogName1");
            Assertions.assertEquals(mapping2.getJavaType(), "java.lang.Object");
            var mapping3 = stmt.getSqlSource().getParameterMappings().get(2);
            Assertions.assertEquals(mapping3.getProperty(), "description1");
            Assertions.assertEquals(mapping3.getJavaType(), "java.lang.Object");
            var mapping4 = stmt.getSqlSource().getParameterMappings().get(3);
            Assertions.assertEquals(mapping4.getProperty(), "createTime1");
            Assertions.assertEquals(mapping4.getJavaType(), "java.lang.Object");
            Assertions.assertEquals(stmt.getResultMaps().size(), 0);
        }
        @Test
        void wrongParameterTypeTest() {
            var stmt = config.getMappedStatement("db.WrongBlogMapper.insertAuthor");
            Assertions.assertEquals(stmt.getId(), "db.WrongBlogMapper.insertAuthor");
            Assertions.assertEquals(stmt.getParameterMap().getType(), "model.UnknownType");
            Assertions.assertEquals(stmt.getSqlSource().getParameterMappings().size(), 1);
            var mapping1 = stmt.getSqlSource().getParameterMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "name");
            Assertions.assertEquals(mapping1.getJavaType(), "java.lang.Object");
            Assertions.assertEquals(stmt.getResultMaps().size(), 0);
        }
        @Test
        void wrongResultTypeTest() {
            var stmt = config.getMappedStatement("db.WrongBlogMapper.selectAuthor");
            Assertions.assertEquals(stmt.getId(), "db.WrongBlogMapper.selectAuthor");
            Assertions.assertEquals(stmt.getResultMaps().size(), 1);
            var map = stmt.getResultMaps().get(0);
            Assertions.assertEquals(map.getId(), "db.WrongBlogMapper.selectAuthor-Inline");
            Assertions.assertEquals(map.getType(), "model.UnknownType");
        }
        @Test
        void wrongResultMapTest() {
            var stmt = config.getMappedStatement("db.WrongBlogMapper.selectBlog");
            Assertions.assertEquals(stmt.getId(), "db.WrongBlogMapper.selectBlog");
            Assertions.assertEquals(stmt.getResultMaps().size(), 0);
        }
        @Test
        void wrongPropertyAndTypeInResultMapTest() {
            var stmt = config.getMappedStatement("db.WrongBlogMapper.selectBlogByAuthor");
            Assertions.assertEquals(stmt.getId(), "db.WrongBlogMapper.selectBlogByAuthor");
            Assertions.assertEquals(stmt.getResultMaps().size(), 1);
            var result = stmt.getResultMaps().get(0);
            Assertions.assertEquals(result.getId(), "db.WrongBlogMapper.blogResult");
            Assertions.assertEquals(result.getType(), "model.unknownType");
            var mapping1 = result.getResultMappings().get(0);
            Assertions.assertEquals(mapping1.getProperty(), "id1");
            Assertions.assertEquals(mapping1.getJavaType(), null);
            var mapping2 = result.getResultMappings().get(1);
            Assertions.assertEquals(mapping2.getProperty(), "blogName1");
            Assertions.assertEquals(mapping2.getJavaType(), null);
            var mapping3 = result.getResultMappings().get(2);
            Assertions.assertEquals(mapping3.getProperty(), "description1");
            Assertions.assertEquals(mapping3.getJavaType(), null);
            var mapping4 = result.getResultMappings().get(3);
            Assertions.assertEquals(mapping4.getProperty(), "createTime1");
            Assertions.assertEquals(mapping4.getJavaType(), null);
            var mapping5 = result.getResultMappings().get(4);
            Assertions.assertEquals(mapping5.getProperty(), "author1");
            Assertions.assertEquals(mapping5.getJavaType(), null);
            var mapping6 = result.getResultMappings().get(5);
            Assertions.assertEquals(mapping6.getProperty(), "contents1");
            Assertions.assertEquals(mapping6.getColumnPrefix(), "content_");
            Assertions.assertEquals(mapping6.getNestedResultMapId(), "db.WrongBlogMapper.contentResult");
            Assertions.assertEquals(mapping6.getJavaType(), null);
        }
    }

    @Test
    void printTypesInMappedStatement() throws IOException, URISyntaxException, ConfigNotFoundException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root);
        var path = server.getConfigFile();
        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        var config = parser.parse();
        var stmts = config.getMappedStatements().stream().filter(m -> m.getId().contains("db.BlogMapper")).collect(Collectors.toList());
        for (var stmt : stmts) {
            var map = stmt.getParameterMap();
            Optional<ClassType> classType = Optional.empty();
            logger.info(stmt.getId());
            if (map.getType() != null) {
                var parameter = map.getType();
                var finder = server.javaProject.createTypeFinder();
                classType = finder.findType(parameter);
                logger.info("parameterType: " + parameter);
                logger.info("parameterType: " + classType.toString());
                Assertions.assertTrue(classType.isPresent());
            }
            for (var mapping : stmt.getSqlSource().getParameterMappings()) {
                var exist = classType.map(t -> t.fieldMap.containsKey(mapping.getProperty())).orElse(false);
                logger.info(mapping.getProperty() + ":" + mapping.getJavaType() + (exist ? " is a field of " + classType.get().fullName : " is not a field of " + classType.get().fullName));
            }
            stmt.getResultMaps().stream().findFirst().ifPresent((resultMap) -> {
                var parameter = resultMap.getType();
                var finder = server.javaProject.createTypeFinder();
                var classTypeInResultMap = finder.findType(parameter);
                Assertions.assertTrue(classTypeInResultMap.isPresent());
                logger.info("resultType: " + classTypeInResultMap.get().toString());
            });
            logger.info("");
            logger.info("");
        }
    }

    @Test
    void parsingTest() throws URISyntaxException, IOException, ConfigNotFoundException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService();
        server.initialize(root);
        var path = server.getConfigFile();
        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        var config = parser.parse();
        Assertions.assertNotNull(config);
        logger.debug(config.toString());
    }

    @Test
    void mybatisConfigNotFoundTest() {
        Assertions.assertThrows(Exception.class, () -> {
            var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/java-project").toURI()).normalize();
            var server = new MyBatisProjectService();
            server.initialize(root);
        });
    }
}