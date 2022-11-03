package els.project;

import els.exception.JavaProjectInitializationError;
import org.eclipse.aether.artifact.Artifact;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qwefgh90
 */
public class MavenProject extends JavaProject {
    protected final Path pomFile;
    protected MavenProjectUpdater updater;
    /*public MavenProject(Path root, Set<Artifact> dependencies, Set<Path> docs, Set<Path> classPath, Set<String> exports, Set<String> externalDependencies, Path pomFile, MavenProjectUpdater updater) throws JavaProjectInitializationError {
        super(root, dependencies.stream().map(d -> d.getFile().toPath()).collect(Collectors.toSet()), docs, classPath, exports, externalDependencies);
        this.pomFile = pomFile;
        this.updater = updater;
    }*/
    public MavenProject(Path root, Set<Path> dependencies, Set<Path> docs, Set<Path> classPath, Set<String> exports, Set<String> externalDependencies, Path pomFile, MavenProjectUpdater updater) throws JavaProjectInitializationError {
        super(root, dependencies, docs, classPath, exports, externalDependencies);
        this.pomFile = pomFile;
        this.updater = updater;
    }
}
