package mybatis.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class ParserUtilTest {
    @Test
    void utilTest() throws URISyntaxException {
        var root1 = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1/pom.xml").toURI()).normalize();
        Assertions.assertFalse(ProjectUtil.isMyBatisConfigFile(root1));
        var root2 = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1/src/main/resources/config.xml").toURI()).normalize();
        Assertions.assertTrue(ProjectUtil.isMyBatisConfigFile(root2));
        var root3 = Paths.get("asdfasdf").normalize();
        Assertions.assertFalse(ProjectUtil.isMyBatisConfigFile(root3));
    }
}
