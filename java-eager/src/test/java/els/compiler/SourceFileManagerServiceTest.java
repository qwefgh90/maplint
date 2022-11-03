package els.compiler;

import els.JavaLanguageService;
import els.exception.JavaProjectInitializationError;
import els.project.maven.MavenUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author qwefgh90
 */
public class SourceFileManagerServiceTest {

    @Test
    public void readJavaFileObjects() throws URISyntaxException, IOException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var project = languageService.createJavaProject(root);
        var extraFileService = project.getMetaFileManager();
        var module = extraFileService.getLocationForModule(StandardLocation.MODULE_SOURCE_PATH, "java.base");
        var dep= extraFileService.getJavaFileForInput(StandardLocation.SOURCE_PATH, "com.mysql.cj.MysqlConnection", JavaFileObject.Kind.SOURCE);
        var jdk = extraFileService.getJavaFileForInput(StandardLocation.SOURCE_PATH, "java.util.List", JavaFileObject.Kind.SOURCE);
        var user = extraFileService.getJavaFileForInput(StandardLocation.SOURCE_PATH, "model.Author", JavaFileObject.Kind.SOURCE);
        Assertions.assertNotNull(module);
        Assertions.assertNotNull(dep);
        Assertions.assertNotNull(jdk);
        Assertions.assertNotNull(user);
    }

    @Test
    public void mavenExample() throws Exception {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        MavenUtils.dependencies(Files.createTempDirectory("local-repository"), root);
    }
}
