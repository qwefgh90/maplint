package els.project;

import els.JavaLanguageService;
import els.exception.JavaProjectInitializationError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * @author qwefgh90
 */
public abstract class JavaProjectFactory {
    protected final Path root;

    protected JavaProjectFactory(Path root) {
        this.root = root;
    }

    /**
     * Get a factory instance which the type is decided with root files.
     * @param root
     * @return
     */
    public static JavaProjectFactory getFactory(Path root){
        if(Files.exists(root.resolve("pom.xml"))){
            return new MavenProjectFactory(root);
        }
        throw new UnsupportedOperationException("Other types of the project isn't supported currently. " + root);
    }

    public JavaProject create() throws JavaProjectInitializationError {
        return create(Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    public abstract JavaProject create(Set<String> externalDependencies, Set<Path> classPath, Set<String> exports) throws JavaProjectInitializationError;
}
