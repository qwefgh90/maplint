package els.project;

import els.exception.JavaProjectInitializationError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * @author qwefgh90
 */
public abstract class JavaProjectFactory {
    protected final Path projectRoot;
    protected final Path workspace;

    protected JavaProjectFactory(Path workspace, Path projectRoot) {
        this.projectRoot = projectRoot;
        this.workspace = workspace;
    }

    /**
     * Get a factory instance which the type is decided with root files.
     * @param root
     * @return
     */
    public static JavaProjectFactory getFactory(Path workspace, Path root){
        if(Files.exists(root.resolve("pom.xml"))){
            return new MavenProjectFactory(workspace, root);
        }
        throw new UnsupportedOperationException("Other types of the project isn't supported yet except for Maven. " + root);
    }

    public JavaProject create() throws JavaProjectInitializationError {
        return create(Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    /**
     * TODO: Define parameters
     * @param externalDependencies
     * @param classPath
     * @param exports
     * @return
     * @throws JavaProjectInitializationError
     */
    public abstract JavaProject create(Set<String> externalDependencies, Set<Path> classPath, Set<String> exports) throws JavaProjectInitializationError;
}
