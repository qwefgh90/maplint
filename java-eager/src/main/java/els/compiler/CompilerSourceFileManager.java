package els.compiler;

import els.project.JavaProject;
import els.project.SourceFile;
import org.javacs.StringSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 *
 */
public class CompilerSourceFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private static final Logger logger = LoggerFactory.getLogger(CompilerSourceFileManager.class);

    public final JavaProject javaProject;
    public CompilerSourceFileManager(JavaProject javaProject){
        super(createDelegateFileManager());
        this.javaProject = javaProject;
    }

    private static StandardJavaFileManager createDelegateFileManager() {
        var compiler = ServiceLoader.load(JavaCompiler.class).iterator().next();
        return compiler.getStandardFileManager(CompilerSourceFileManager::logError, null, Charset.defaultCharset());
    }

    private static void logError(Diagnostic<?> error) {
        logger.warn(error.getMessage(null));
    }

    @Override
    public Iterable<JavaFileObject> list(
            Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (location == StandardLocation.SOURCE_PATH) {
            var stream = javaProject.getJavaFiles(packageName).stream().map(f -> asJavaFileObject(f.path));
            return stream::iterator;
        } else {
            return super.list(location, packageName, kinds, recurse);
        }
    }

    private JavaFileObject asJavaFileObject(Path file) {
        if(!file.isAbsolute())
            throw new RuntimeException("The relative path is not allowd. " +  file.toString());
        // TODO erase method bodies of files that are not open
        return new SourceFileObject(file);
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (location == StandardLocation.SOURCE_PATH) {
            var source = (SourceFileObject) file;
            var path = Paths.get(source.toUri());
            var projectFile = javaProject.getFile(path);
            if(projectFile instanceof SourceFile){
                return ((SourceFile) projectFile).fullName;
            }
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public boolean hasLocation(Location location) {
        return location == StandardLocation.SOURCE_PATH || super.hasLocation(location);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind)
            throws IOException {
        // javaProject shadows disk
        if (location == StandardLocation.SOURCE_PATH) {
            var packageName = StringSearch.mostName(className);
            var simpleName = StringSearch.lastName(className);
            for (var f : javaProject.getJavaFiles(packageName)) {
                if (f.path.getFileName().toString().equals(simpleName + kind.extension)) {
                    return new SourceFileObject(f.path);
                }
            }
            // Fall through to disk in case we have .jar or .zip files on the source path
        }
        return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        if (location == StandardLocation.SOURCE_PATH) {
            return null;
        }
        return super.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public boolean contains(Location location, FileObject file) throws IOException {
        if (location == StandardLocation.SOURCE_PATH) {
            var source = (SourceFileObject) file;
            return javaProject.contains(Paths.get(source.toUri()));
        } else {
            return super.contains(location, file);
        }
    }

    void setLocation(Location location, Iterable<? extends File> files) throws IOException {
        fileManager.setLocation(location, files);
    }

    void setLocationFromPaths(Location location, Collection<? extends Path> searchpath) throws IOException {
        fileManager.setLocationFromPaths(location, searchpath);
    }
}
