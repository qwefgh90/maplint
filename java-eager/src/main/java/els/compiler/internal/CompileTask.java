package els.compiler.internal;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTaskImpl;
import els.compiler.Parser;
import els.project.JavaProject;
import els.project.SourceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CompileTask {
    Logger logger = LoggerFactory.getLogger(CompileTask.class);

    // The compilation parameters are below.
    public final JavaFileManager fileManager;
    public final Set<Path> classPath;
    public final Set<String> addExports;
    public final Set<? extends JavaFileObject> roots;
    public final List<String> options;
    public final JavaProject javaProject;

    // The compilation results are below.
    private List<CompilationUnitTree> compilationUnitTreeList;
    private Trees trees;
    private Elements elements;
    private Types types;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;
    private Set<Path> unresolvedResources;
    private final JavacTaskImpl javacTask;
    Runnable signalEndToCompiler;
    InternalCompiler.ReusableContext ctx;
    CompileParameter compileParameter;

    private boolean closed;

    protected CompileTask(
            CompileParameter parameter,
            List<Diagnostic<? extends JavaFileObject>> diagnostics,
            JavacTaskImpl javacTask,
            InternalCompiler.ReusableContext ctx, Runnable signalEndToCompiler,
            List<String> options) {
        this.compileParameter = parameter;
        this.fileManager = parameter.fileManager;
        this.javaProject = parameter.javaProject;
        this.classPath = parameter.classPath;
        this.addExports = parameter.addExports;
        this.roots = parameter.files;
        this.options = options;
        this.javacTask = javacTask;
        this.diagnostics = diagnostics;
        this.signalEndToCompiler = signalEndToCompiler;
        this.ctx = ctx;
//        this.internalCompileTask = reusableCompiler.createInternalCompileTask(fileManager, diagnostics::add, options, List.of(), roots);
    }

    public CompileParameter getCompileParameter() {
        return compileParameter;
    }

    public boolean isClosed() {
        return closed;
    }

    public JavaCompiler.CompilationTask getJavacTask() {
        return javacTask;
    }

    public Trees getTrees() {
        return trees;
    }

    public Elements getElements() {
        return elements;
    }

    public Types getTypes() {
        return types;
    }

    public List<CompilationUnitTree> getCompilationUnitTreeList() {
        return compilationUnitTreeList;
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }

    public Set<Path> getUnresolvedResources() {
        return unresolvedResources;
    }

    private CompileResult result;

    public CompileResult start() throws IOException {
        if (this.closed)
            return result;
        // Compile all roots
        var compilationUnitTreeList = new ArrayList<CompilationUnitTree>();
        for (var t : javacTask.parse()) {
            compilationUnitTreeList.add(t);
        }
        // The results of borrow.task.analyze() are unreliable when errors are present
        // You can get at `Element` values using `Trees`
        javacTask.analyze();
        this.trees = Trees.instance(javacTask);
        this.elements = javacTask.getElements();
        this.types = javacTask.getTypes();
        this.compilationUnitTreeList = Collections.unmodifiableList(compilationUnitTreeList);
        this.unresolvedResources = Collections.unmodifiableSet(checkUnresolvedResources());
//        this.diagnostics = Collections.unmodifiableList(this.diagnostics);
//        internalCompileTask.close();

        ctx.clear();
        try {
            var method = JavacTaskImpl.class.getDeclaredMethod("cleanup");
            method.setAccessible(true);
            method.invoke(javacTask);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        signalEndToCompiler.run();
        this.closed = true;

        result = new CompileResult(compileParameter, compilationUnitTreeList, trees, elements, types, diagnostics, unresolvedResources);
        return result;
    }

    public CompileResult getResult() {
        return result;
    }

    /**
     * If the compilation failed because javac didn't find some package-private files in source files with different
     * names, list those source files.
     */
    private HashSet<Path> checkUnresolvedResources() {
        // Check for "class not found errors" that refer to package private classes
        var addFiles = new HashSet<Path>();
        if (diagnostics.size() > 0)
            logger.error("The error count is {}", diagnostics.size());
        for (var diagnostic : diagnostics)
            logger.error("DIAGNOSTIC MESSAGE =>\n{}", diagnostic.toString());
        for (var err : diagnostics) {
            if (!err.getCode().equals("compiler.err.cant.resolve.location")) continue;
            if (!isValidFileRange(err)) continue;
            var className = errorText(err);
            var packageName = packageName(err);
            var location = findPackagePrivateClass(packageName, className);
            if (location != FILE_NOT_FOUND) {
                addFiles.add(location);
            }
        }
        return addFiles;
    }

    private String errorText(Diagnostic<? extends JavaFileObject> err) {
        var file = Paths.get(err.getSource().toUri());
        var projectFile = javaProject.getFile(file);
        var contents = javaProject.getSourceContent(file);
        if (contents != null) {
            var begin = (int) err.getStartPosition();
            var end = (int) err.getEndPosition();
            return contents.substring(begin, end);
        }
        return null;
    }

    private String packageName(Diagnostic<? extends JavaFileObject> err) {
        var file = Paths.get(err.getSource().toUri());
        var projectFile = javaProject.getFile(file);
        if (projectFile != null && projectFile.fileKind == JavaFileObject.Kind.SOURCE) {
            return ((SourceFile) projectFile).packageName;
        }
        return null;
    }

    private static final Path FILE_NOT_FOUND = Paths.get("");

    private Path findPackagePrivateClass(String packageName, String className) {
        for (var file : javaProject.getJavaFiles(packageName)) {
            var parse = Parser.parseFile(file.path);
            for (var candidate : parse.packagePrivateClasses()) {
                if (candidate.contentEquals(className)) {
                    return file.path;
                }
            }
        }
        return FILE_NOT_FOUND;
    }

    private boolean isValidFileRange(Diagnostic<? extends JavaFileObject> d) {
        return d.getSource().toUri().getScheme().equals("file") && d.getStartPosition() >= 0 && d.getEndPosition() >= 0;
    }

}
