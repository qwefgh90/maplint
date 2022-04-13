package els.compiler;

import els.project.JavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qwefgh90
 */
public class MetaFileManager extends CompilerSourceFileManager {
    private static final Logger logger = LoggerFactory.getLogger(MetaFileManager.class);

    public FileSystem srcZipfs;
    public Path srcZipRoot;
    public List<String> jdkModuleList;

    public MetaFileManager(JavaProject javaProject) {
        super(javaProject);
    }

    public void update(Set<Path> docs) throws IOException {
        // Path to source .jars + src.zip
        var sources = new HashSet<>(docs);
        this.srcZipfs = createZipFileSystemForSrcZip();
        this.srcZipRoot = srcZipfs.getPath("/");

        if (srcZipRoot != null) {
            var jdkModuleList = new ArrayList<String>();
            for (var entry : Files.list(srcZipRoot).collect(Collectors.toList())) {
                jdkModuleList.add(entry.getFileName().toString());
                sources.add(entry);
            }
            this.jdkModuleList = Collections.unmodifiableList(jdkModuleList);
            this.setLocationFromPaths(StandardLocation.MODULE_SOURCE_PATH, Set.of(srcZipRoot));
        }
        this.setLocationFromPaths(StandardLocation.SOURCE_PATH, sources);
    }

    private Path cacheSrcZip;

    private FileSystem createZipFileSystemForSrcZip() throws IOException {
        if (cacheSrcZip == null) {
            cacheSrcZip = findSrcZipPath();
            if (cacheSrcZip == null) {
                return null;
            }
        }
        var fs = FileSystems.newFileSystem(cacheSrcZip, CompilerSourceFileManager.class.getClassLoader());
        return fs;
    }

    private Path findSrcZipPath() {
        var javaHome = JavaHomeHelper.javaHome();
        String[] locations = {
                "lib/src.zip", "src.zip",
        };
        for (var rel : locations) {
            var srcZipPath = javaHome.resolve(rel);
            if (Files.exists(srcZipPath)) {
                logger.debug("The zip file's been found in {}", srcZipPath);
                return srcZipPath;
            }
        }
        logger.warn("Couldn't find src.zip in {}", javaHome);
        return null;
    }

}
