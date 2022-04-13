package els.project;

import els.exception.JavaProjectInitializationError;

import java.nio.file.Path;
import java.util.Set;

/**
 * @author qwefgh90
 */
public class MavenProject extends JavaProject {
    protected final Path pomFile;
    protected MavenProjectUpdater updater;
    public MavenProject(Path root, Set<Path> dependencies, Set<Path> docs, Set<Path> classPath, Set<String> exports, Set<String> externalDependencies, Path pomFile, MavenProjectUpdater updater) throws JavaProjectInitializationError {
        super(root, dependencies, docs, classPath, exports, externalDependencies);
        this.pomFile = pomFile;
        this.updater = updater;
    }
}
