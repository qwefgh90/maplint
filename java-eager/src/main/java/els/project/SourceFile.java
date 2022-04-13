package els.project;


import javax.tools.JavaFileObject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class SourceFile extends ProjectFile {
    public final Instant modified;
    public final String packageName;
    public final Path path;
    public final Path relativePackagePath;
    public final String simpleName;
    public final String fullName;

    SourceFile(Instant modified, String packageName, Path path) {
        super(JavaFileObject.Kind.SOURCE, path);
        this.modified = modified;
        this.packageName = packageName;
        this.path = path;
        this.relativePackagePath = Paths.get(packageName.replaceAll("[.]", "/"), path.getFileName().toString());
        this.simpleName = removeExtension(path.getFileName().toString());
        this.fullName = packageName + "." + simpleName;
    }

    private String removeExtension(String fileName) {
        var lastDot = fileName.lastIndexOf(".");
        return (lastDot == -1 ? fileName : fileName.substring(0, lastDot));
    }
}