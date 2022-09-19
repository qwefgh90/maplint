package els.compiler;

import els.compiler.internal.CompileParameter;
import els.compiler.internal.CompileResult;
import els.compiler.internal.CompileTask;
import els.compiler.internal.InternalCompiler;
import els.project.JavaProject;
import els.project.SourceFile;
import els.service.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * It includes compiler functionalities for other useful services.
 */
public class CompilerService {
    Logger logger = LoggerFactory.getLogger(CompilerService.class);
    private final InternalCompiler internalCompiler = new InternalCompiler();
    private final CompilerSourceFileManager sourceFileManager;
    private final JavaProject javaProject;

    public CompilerService(JavaProject javaProject, CompilerSourceFileManager sourceFileManager) {
        this.sourceFileManager = sourceFileManager;
        this.javaProject = javaProject;
    }

    public CompileResult compile(Path... files) throws UnexpectedCompileException {
        var sources = new HashSet<JavaFileObject>();
        for (var f : files) {
            sources.add(new SourceFileObject(f));
        }
        return compile(sources);
    }

    public CompileResult compile(Set<? extends JavaFileObject> sources) throws UnexpectedCompileException {
        if (isAlreadyCompiled(sources)) {
            logger.debug("It's going to use a cached compile task for this compilation.");
        } else {
            resetCompileTaskCache();
            lastCompileTask = this.internalCompiler.checkoutCompileTask(new CompileParameter(javaProject.getClassPath(), javaProject.getExports(), this.sourceFileManager, sources, javaProject));
            for (var f : sources) {
                lastCompileModified.put(f, f.getLastModified());
            }
        }
        try {
            return lastCompileTask.start();
        } catch (IOException e) {
            throw new UnexpectedCompileException(String.format("Compilation failed with \n %s"
                    , String.join(",", sources.stream().map(s -> s.toString()).collect(Collectors.toList())))
                    , e);
        }
    }


    public Optional<SourceFileObject> fileUserSourceFileObject(String className) {
//        var fastFind = findPath(className);
//        if (fastFind != NOT_FOUND) return Optional.of(new SourceFileObject(fastFind));
        // In principle, the slow path can be skipped in many cases.
        // If we're spending a lot of time in findTypeDeclaration, this would be a good optimization.
        var packageName = packageName(className);
        var simpleName = simpleName(className);
        for (var f : this.javaProject.getJavaFiles(packageName)) {
            if (containsWord(f.path, simpleName) && containsWord(f.path, className)) {
                return Optional.of(new SourceFileObject(f.path));
            }
        }
        return Optional.empty();
    }


    public Path[] findMemberReferences(String className, String memberName) {
        var candidates = new ArrayList<Path>();
        for (var f : javaProject.getJavaFiles()) {
            if (containsWord(f.path, memberName)) {
                candidates.add(f.path);
            }
        }
        return candidates.toArray(Path[]::new);
    }

    public Optional<JavaFileObject> findPublicTypeDeclarationInDocPath(String className) {
        try {
            var found =
                    sourceFileManager.getJavaFileForInput(
                            StandardLocation.SOURCE_PATH, className, JavaFileObject.Kind.SOURCE);
            return Optional.ofNullable(found);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private CompileTask lastCompileTask;
    private Map<JavaFileObject, Long> lastCompileModified = new HashMap<>();

    private void resetCompileTaskCache() {
        lastCompileModified.clear();
    }

    private boolean isAlreadyCompiled(Set<? extends JavaFileObject> sources) {
        if (lastCompileModified.size() != sources.size()) {
            return false;
        }
        for (var f : sources) {
            if (!lastCompileModified.containsKey(f)) {
                return false;
            }
            if (f.getLastModified() != lastCompileModified.get(f)) {
                return false;
            }
        }
        return true;
    }

    private static final Pattern PACKAGE_EXTRACTOR = Pattern.compile("^([a-z][_a-zA-Z0-9]*\\.)*[a-z][_a-zA-Z0-9]*");

    private boolean containsImport(Path path, String className) {
        var packageName = packageName(className);
        var sourceFile = this.javaProject.getFile(path);
        if (sourceFile instanceof SourceFile
                && ((SourceFile) sourceFile).packageName.equals(packageName))
            return true;
        var star = packageName + ".*";
        for (var i : readImports(path)) {
            if (i.equals(className) || i.equals(star)) return true;
        }
        return false;
    }

    //
    private String packageName(String className) {
        var m = PACKAGE_EXTRACTOR.matcher(className);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    private static final Pattern SIMPLE_EXTRACTOR = Pattern.compile("[A-Z][_a-zA-Z0-9]*$");

    private String simpleName(String className) {
        var m = SIMPLE_EXTRACTOR.matcher(className);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    private static final Cache<String, Boolean> cacheContainsWord = new Cache<>();

    private boolean containsWord(Path file, String word) {
        if (cacheContainsWord.needs(file, word)) {
            try {
                cacheContainsWord.load(file, word, Files.readString(file).contains(word));
            } catch (IOException e) {
                return false;
            }
        }
        return cacheContainsWord.get(file, word);
    }

    private static final Cache<Void, List<String>> cacheContainsType = new Cache<>();
//
//    private boolean containsType(Path file, String className) {
//        if (cacheContainsType.needs(file, null)) {
//            var root = parse(file).root;
//            var types = new ArrayList<String>();
//            new FindTypeDeclarations().scan(root, types);
//            cacheContainsType.load(file, null, types);
//        }
//        return cacheContainsType.get(file, null).contains(className);
//    }

    private Cache<Void, List<String>> cacheFileImports = new Cache<>();

    private List<String> readImports(Path file) {
        if (cacheFileImports.needs(file, null)) {
            loadImports(file);
        }
        return cacheFileImports.get(file, null);
    }

    private void loadImports(Path file) {
        var list = new ArrayList<String>();
        var importClass = Pattern.compile("^import +([\\w\\.]+\\.\\w+);");
        var importStar = Pattern.compile("^import +([\\w\\.]+\\.\\*);");
        try (var lines = Files.newBufferedReader(file)) {
            for (var line = lines.readLine(); line != null; line = lines.readLine()) {
                // If we reach a class declaration, stop looking for imports
                // TODO This could be a little more specific
                if (line.contains("class")) break;
                // import foo.bar.Doh;
                var matchesClass = importClass.matcher(line);
                if (matchesClass.matches()) {
                    list.add(matchesClass.group(1));
                }
                // import foo.bar.*
                var matchesStar = importStar.matcher(line);
                if (matchesStar.matches()) {
                    list.add(matchesStar.group(1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cacheFileImports.load(file, null, list);
    }

    Path NOT_FOUND = Paths.get("");
}
