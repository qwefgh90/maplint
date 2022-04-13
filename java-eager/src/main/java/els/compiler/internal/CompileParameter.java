package els.compiler.internal;

import els.project.JavaProject;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CompileParameter {
    public final Set<Path> classPath;
    public final Set<String> addExports;
    public final JavaFileManager fileManager;
    public final Set<? extends JavaFileObject> files;
    public final JavaProject javaProject;
    public final List<String> options;

    public CompileParameter(Set<Path> classPath, Set<String> addExports, JavaFileManager fileManager, Set<? extends JavaFileObject> files, JavaProject javaProject) {
        this.classPath = Collections.unmodifiableSet(classPath);
        this.addExports = Collections.unmodifiableSet(addExports);
        this.fileManager = fileManager;
        this.files = Collections.unmodifiableSet(files);
        this.javaProject = javaProject;
        this.options = StreamSupport.stream(
                Collections.unmodifiableList(options(classPath, addExports)).spliterator(),
                false).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<String> options(Set<Path> classPath, Set<String> addExports) {
        var list = new ArrayList<String>();

        Collections.addAll(list, "-classpath", joinPath(classPath));
        Collections.addAll(list, "--add-modules", "ALL-MODULE-PATH");
        // Collections.addAll(list, "-verbose");
        Collections.addAll(list, "-proc:none");
        Collections.addAll(list, "-g");
        // You would think we could do -Xlint:all,
        // but some lints trigger fatal errors in the presence of parse errors
        Collections.addAll(
                list,
                "-Xlint:cast",
                "-Xlint:deprecation",
                "-Xlint:empty",
                "-Xlint:fallthrough",
                "-Xlint:finally",
                "-Xlint:path",
                "-Xlint:unchecked",
                "-Xlint:varargs",
                "-Xlint:static");
        for (var export : addExports) {
            list.add("--add-exports");
            list.add(export + "=ALL-UNNAMED");
        }

        return list;
    }

    /**
     * Combine source path or class path entries using the system separator, for example ':' in unix
     */
    private static String joinPath(Collection<Path> classOrSourcePath) {
        return classOrSourcePath.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
    }
}
