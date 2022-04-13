package mybatis.project;

import els.JavaLanguageService;
import els.exception.JavaProjectInitializationError;
import els.service.TypeFinder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class WrapperClassTypeTest {
    private static final Logger logger = LoggerFactory.getLogger(WrapperClassTypeTest.class);

    @Test
    void constructTest() throws URISyntaxException, ConfigNotFoundException, MyBatisProjectInitializationException {
        var path = ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI();
        var root = Paths.get(path).normalize();
        var server = JavaLanguageService.getLanguageService();
//        server.initialize(root);
        TypeFinder finder;
        try {
            finder = server.createJavaProject(root).createTypeFinder();
        } catch (
                JavaProjectInitializationError e) {
            throw new MyBatisProjectInitializationException(e);
        }
        var classType1 = finder.findType("model.Author");
        var wrapperClass = WrapperClassType.create(classType1.get());
        Assertions.assertEquals(wrapperClass.getGetterType("id").fullName, "int");
        Assertions.assertEquals(wrapperClass.getGetterType("name").fullName, "java.lang.String");
        Assertions.assertTrue(wrapperClass.hasGetter("id"));
        Assertions.assertTrue(wrapperClass.hasSetter("id"));
        Assertions.assertTrue(wrapperClass.hasGetter("name"));
        Assertions.assertTrue(wrapperClass.hasSetter("name"));
        logger.info(wrapperClass.toString());
    }
}
