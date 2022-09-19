package els.project;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class ProjectFile {
    public final JavaFileObject.Kind fileKind;
    public final Path path;

    protected ProjectFile(JavaFileObject.Kind fileKind, Path path) {
        this.fileKind = fileKind;
        this.path = path;
    }

    protected static String packageName(Path file) {
        var packagePattern = Pattern.compile("^package +([^ ]*);");
        var startOfClass = Pattern.compile("^[\\w ]*class +\\w+");
        try (var lines = Files.newBufferedReader(file)) {
            for (var line = lines.readLine(); line != null; line = lines.readLine()) {
                if (startOfClass.matcher(line).find()) return "";
                var matchPackage = packagePattern.matcher(line);
                if (matchPackage.matches()) {
                    var id = matchPackage.group(1);
                    return id;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO fall back on parsing file
        return "";
    }

    public static ProjectFile create(Path path) throws IOException {
        if(isJavaFile(path)) {
            var time = Files.getLastModifiedTime(path).toInstant();
            var packageName = packageName(path);
            return new SourceFile(time, packageName, path);
        }else if(isClassFile(path)){
            return new ProjectFile(JavaFileObject.Kind.CLASS, path);
        }else if(isHTMLFile(path)){
            return new ProjectFile(JavaFileObject.Kind.HTML, path);
        }else{
            return new ProjectFile(JavaFileObject.Kind.OTHER, path);
        }
    }

    private static boolean isJavaFile(Path file) {
        var name = file.getFileName().toString();
        // We hide module-info.java from javac, because when javac sees module-info.java
        // it goes into "module mode" and starts looking for classes on the module class path.
        // This becomes evident when javac starts recompiling *way too much* on each task,
        // because it doesn't realize there are already up-to-date .class files.
        // The better solution would be for java-language server to detect the presence of module-info.java,
        // and go into its own "module mode" where it infers a module source path and a module class path.
        return name.endsWith(".java") && !Files.isDirectory(file) && !name.equals("module-info.java");
    }

    private static boolean isHTMLFile(Path file) {
        var name = file.getFileName().toString();
        return name.endsWith(".html") && !Files.isDirectory(file);
    }
    private static boolean isClassFile(Path file) {
        var name = file.getFileName().toString();
        return name.endsWith(".class") && !Files.isDirectory(file);
    }

}
