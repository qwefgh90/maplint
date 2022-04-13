package els.project;

import els.compiler.CompilerService;
import els.compiler.CompilerSourceFileManager;
import els.compiler.MetaFileManager;
import els.exception.JavaProjectInitializationError;
import els.service.TypeFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * It is the large object representing the project and the file tree.
 * It provides various services related in this project.
 * The work of processing the file tree is delegated to fileContainer.
 *
 * @author qwefgh90
 */
public abstract class JavaProject implements AutoCloseable {
    Logger logger = LoggerFactory.getLogger(JavaProject.class);
    protected final Path root;
    protected final FileWatcher fileContainer;
    protected final CompilerSourceFileManager compilerSourceFileManager;
    protected final MetaFileManager metaFileManager;
    protected Set<Path> dependencies;
    protected Set<Path> docs;
    protected Set<Path> userClassPath;
    protected Set<String> exports;
    protected Set<String> externalDependencies;
    protected ProjectStatus status = ProjectStatus.INITIALIZED;

    public JavaProject(Path root, Set<Path> dependencies, Set<Path> docs, Set<Path> userClassPath, Set<String> exports, Set<String> externalDependencies) throws JavaProjectInitializationError {
        try {
            this.root = root;
            this.fileContainer = new FileWatcher(root);
            this.compilerSourceFileManager = new CompilerSourceFileManager(this);
            this.metaFileManager = new MetaFileManager(this);
            updateProperties(dependencies,
                    docs,
                    userClassPath,
                    exports,
                    externalDependencies);
            watchRootPath();
        } catch (IOException e) {
            try {
                this.close();
                throw new JavaProjectInitializationError(e);
            } catch (IOException ex) {
                throw new JavaProjectInitializationError(ex);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            if (fileContainer != null)
                fileContainer.close();
            closed = true;
            status = ProjectStatus.CLOSED;
        }
    }

    public Path getRoot() {
        return root;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Set<Path> getDependencies() {
        return dependencies;
    }

    public Set<Path> getDocs() {
        return docs;
    }

    public Set<Path> getUserClassPath() {
        return userClassPath;
    }

    public Set<Path> getClassPath() {
        return Stream.concat(this.userClassPath.stream(), this.dependencies.stream()).collect(Collectors.toSet());
    }

    public Set<String> getExports() {
        return exports;
    }

    public Set<String> getExternalDependencies() {
        return externalDependencies;
    }

    public Set<SourceFile> getJavaFiles(String packageName) {
        var javaSources = fileContainer.getJavaSources();
        return javaSources.values().stream().filter(file -> {
            if (file.packageName.equals(packageName)) {
                return true;
            }
            return false;
        }).collect(Collectors.toSet());
    }

    public Set<ProjectFile> getFiles() {
        var otherFiles = getOtherFiles();
        otherFiles.addAll(getJavaFiles());
        return otherFiles;
    }

    public ProjectFile getFile(Path key) {
        if (this.fileContainer.getJavaSources().containsKey(key))
            return this.fileContainer.getJavaSources().get(key);
        else
            return this.fileContainer.getOtherFiles().get(key);
    }

    public boolean contains(Path key) {
        if (this.fileContainer.getJavaSources().containsKey(key))
            return true;
        else
            return this.fileContainer.getOtherFiles().containsKey(key);
    }

    public Set<SourceFile> getJavaFiles() {
        return this.fileContainer.getJavaSources().values().stream().collect(Collectors.toSet());
    }

    public Set<ProjectFile> getOtherFiles() {
        return this.fileContainer.getOtherFiles().values().stream().collect(Collectors.toSet());
    }

    public String getSourceContent(Path path) {
        var file = getFile(path);
        if (file != null && file.fileKind == JavaFileObject.Kind.SOURCE) {
            try {
                return Files.readString(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public MetaFileManager getMetaFileManager() {
        return metaFileManager;
    }

    public TypeFinder createTypeFinder() {
        return new TypeFinder(createCompilerService());
    }

    protected void updateProperties(Set<Path> dependencies, Set<Path> docs, Set<Path> userClassPath, Set<String> exports, Set<String> externalDependencies) throws IOException {
        this.dependencies = Collections.unmodifiableSet(dependencies);
        this.docs = Collections.unmodifiableSet(docs);
        this.userClassPath = Collections.unmodifiableSet(userClassPath);
        this.exports = Collections.unmodifiableSet(exports);
        this.externalDependencies = Collections.unmodifiableSet(externalDependencies);
        metaFileManager.update(docs);
    }

    private boolean closed = false;

    private void watchRootPath() {
        fileContainer.registerCallback(event -> {
            logger.debug("log a event {} ", event.toString());
        });
    }

    protected CompilerService createCompilerService() {
        var compilerService = new CompilerService(this, compilerSourceFileManager);
        return compilerService;
    }
}


