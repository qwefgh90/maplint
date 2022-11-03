package els;

import els.exception.JavaProjectInitializationError;
import els.project.JavaProjectFactory;
import els.project.JavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * JavaLanguageService is the top-level service to support importing the java project.
 *
 * @author qwefgh90
 */
public final class JavaLanguageService {
    private static final Logger logger = LoggerFactory.getLogger(JavaLanguageService.class);
    private Map<Path, JavaProject> projectList = new HashMap<>();
    protected Path workspace;

    protected JavaLanguageService(Path workspace) {
        this.workspace = workspace;
        logger.info("A new language service's been created on {}.", workspace.toAbsolutePath());
    }

    /**
     * It figures out the type of the project.
     * And it creates an instance of JavaProject
     * @param path
     * @return an instance of JavaProject
     * @throws JavaProjectInitializationError
     */
    public JavaProject createJavaProject(Path path) throws JavaProjectInitializationError {
        var factory = JavaProjectFactory.getFactory(workspace, path);
        if (projectList.containsKey(path))
            return projectList.get(path);
        else {
            var project = factory.create(Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
            return projectList.computeIfAbsent(path, (key) -> project);
        }
    }

    public JavaProject createJavaProject(Path path,
                                         Set<String> externalDependencies,
                                         Set<Path> classPath,
                                         Set<String> addExports) throws JavaProjectInitializationError {
        var factory = JavaProjectFactory.getFactory(workspace, path);
        if (projectList.containsKey(path))
            return projectList.get(path);
        else {
            var project = factory.create(externalDependencies, classPath, addExports);
            return projectList.put(path, project);
        }
    }

    /**
     * It creates a instance of JavaLanguageService with a temporary workspace.
     * @return an instance of the language service
     */
    public static JavaLanguageService getLanguageService() {
        try {
            var tempWorkspace = Files.createTempDirectory("java_language_service");
            return new JavaLanguageService(tempWorkspace);
        } catch (IOException e) {
            throw new RuntimeException("It failed to create a temporary directory for the workspace.", e);
        }
    }

    /**
     * It creates a instance of JavaLanguageService with a provided workspace
     * @param workspaceRoot
     * @return an instance of the language service
     */
    public static JavaLanguageService getLanguageService(Path workspaceRoot) {
        return new JavaLanguageService(workspaceRoot);
    }
}
