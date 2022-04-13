package els.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * It watches changes on the file system
 * and update caches when a new change takes place.
 *
 * @author qwefgh90
 */
public class FileWatcher implements AutoCloseable {
    Logger logger = LoggerFactory.getLogger(FileWatcher.class);
    public final Path root;
    private LocalTime lastUpdateDate;
    private WatchService watchService;
    private Executor executor = Executors.newSingleThreadExecutor();

    public FileWatcher(Path root) throws IOException {
        this.root = root;
        invalidateCaches();
        startWatching();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            if(watchService != null)
                watchService.close();
        }
    }

    public Map<Path, SourceFile> getJavaSources() {
        return javaSourcesCache;
    }

    public Map<Path, ProjectFile> getOtherFiles() {
        return otherFilesCache;
    }

    public void registerCallback(Consumer<WatchEvent<?>> cb) {
        callbacks.add(cb);
    }

    public void unregisterCallback(Consumer<WatchEvent<?>> cb) {
        callbacks.remove(cb);
    }

    public LocalTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    private boolean closed = false;

    private HashSet<Consumer<WatchEvent<?>>> callbacks = new HashSet<>();

    private void invalidateCaches() throws IOException {
        lastUpdateDate = LocalTime.now();
        loadFiles(root);
    }

    private void startWatching() throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        WatchKey registerKey = this.root.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        executor.execute(() -> {
            WatchKey key;
            while (!closed) {
                try {
                    key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        logger.debug("An event occurs from the file system\n" + event.toString());
                        for (var cb : callbacks) {
                            cb.accept(event);
                        }
                        invalidateCaches();
                    }
                    key.reset();
                } catch (InterruptedException | IOException e) {
                    logger.error("An error occurs while watching poll events", e);
                }
            }
        });
    }

    private Map<Path, SourceFile> javaSourcesCache = Collections.emptyMap();
    private Map<Path, ProjectFile> otherFilesCache = Collections.emptyMap();

    private void loadFiles(Path root) throws IOException {
        var visitor = new FileTreeVisitor();
        Files.walkFileTree(root, visitor);
        javaSourcesCache = Collections.unmodifiableMap(visitor.javaSources);
        otherFilesCache = Collections.unmodifiableMap(visitor.otherFiles);
    }
}

class FileTreeVisitor extends SimpleFileVisitor<Path> {
    Logger logger = LoggerFactory.getLogger(FileTreeVisitor.class);
    public final TreeMap<Path, SourceFile> javaSources = new TreeMap<>();
    public final TreeMap<Path, ProjectFile> otherFiles = new TreeMap<>();

    public FileTreeVisitor() {
        javaSources.clear();
        otherFiles.clear();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (attrs.isSymbolicLink()) {
            logger.warn("Don't check the symbolic link of " + dir + " for java sources");
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes _attrs) throws IOException {
        var projectFile = ProjectFile.create(path);
        if (projectFile.fileKind == JavaFileObject.Kind.SOURCE) {
            var sourceFileMeta = (SourceFile) projectFile;
            javaSources.put(path.toAbsolutePath(), sourceFileMeta);
            javaSources.put(sourceFileMeta.relativePackagePath, sourceFileMeta);
        } else {
            otherFiles.put(path, projectFile);
        }
        return FileVisitResult.CONTINUE;
    }
}