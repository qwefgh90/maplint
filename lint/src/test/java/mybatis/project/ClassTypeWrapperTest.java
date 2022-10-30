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

public class ClassTypeWrapperTest {
    private static final Logger logger = LoggerFactory.getLogger(ClassTypeWrapperTest.class);

    @Test
    void constructorAndMethodsTest() throws URISyntaxException, JavaProjectInitializationError {
        var path = ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI();
        var root = Paths.get(path).normalize();
        var server = JavaLanguageService.getLanguageService();
        TypeFinder finder;
            finder = server.createJavaProject(root).createTypeFinder();
        var classType1 = finder.findType("model.Author");
        var wrapper = ClassTypeWrapper.create(classType1.get());
        Assertions.assertEquals(wrapper.getGetterType("id").fullName, "int");
        Assertions.assertEquals(wrapper.getGetterType("name").fullName, "java.lang.String");
        Assertions.assertTrue(wrapper.hasGetter("id"));
        Assertions.assertTrue(wrapper.hasSetter("id"));
        Assertions.assertTrue(wrapper.hasGetter("name"));
        Assertions.assertTrue(wrapper.hasSetter("name"));
        logger.info(wrapper.toString());
    }
}
