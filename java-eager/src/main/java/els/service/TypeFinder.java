package els.service;

import com.sun.tools.javac.code.Type;
import els.compiler.internal.CompileResult;
import els.compiler.SourceFileObject;
import els.compiler.CompilerService;
import els.type.ClassType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * An instance of this class works with only one Thread.
 * It finds out what {@link ClassType} is at the path or the full class name or else.
 */
public class TypeFinder {
    private final Logger logger = LoggerFactory.getLogger(TypeFinder.class);
    private final CompilerService compiler;

    public TypeFinder(CompilerService compiler) {
        this.compiler = compiler;
    }

    /**
     * If there exists a file in the given path, it will figure out what the class type is for the file.
     * @param path
     * @return
     */
    public Optional<ClassType> findType(Path path) {
        if (path == null || !Files.exists(path))
            return Optional.empty();
        return findTypeWithSourceFileObject(new SourceFileObject(path));
    }

    /**
     * It finds the source file of a given fullClassName and delegate it to {@link TypeFinder#findTypeWithSourceFileObject(SourceFileObject)}.
     * If it doesn't exist in the file tree,
     * {@link TypeFinder#findTypeWithArgumentType(String)} is called to resolve the type.
     * @param fullClassName ex) java.util.List<String>
     * @return
     */
    public Optional<ClassType> findType(String fullClassName) {
        if (fullClassName == null)
            return Optional.empty();
        var sourceFileOpt = compiler.fileUserSourceFileObject(fullClassName);
        if(sourceFileOpt.isPresent()) {
            return findTypeWithSourceFileObject(sourceFileOpt.get());
        }
        return findTypeWithArgumentType(fullClassName);
    }

    /**
     * Find the class type from the source code.
     * @param clasName the class name in javaCodeText
     * @param javaCodeText the text of the source code
     * @return
     */
    public Optional<ClassType> findType(String clasName, String javaCodeText) {
        Path file = null;
        try {
            file = Files.createTempFile("TemporaryJavaFile", ".java");
            file = Files.move(file, file.getParent().resolve(clasName+".java"));
            return findTypeWithSourceFileObject(new SourceFileObject(file, javaCodeText, Instant.now()));
        } catch(IOException e){
            return Optional.empty();
        }finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) { logger.error(e.toString());}
        }
    }

    private Optional<ClassType> findTypeWithArgumentType(String fullClassName){
        var temporaryClassName = "Temporary" + (Math.random() * 1000000);
        try {
            temporaryClassName = Files.createTempFile("Temporary","").getFileName().toString();
        } catch (IOException e) {
            logger.warn("It failed to generate the temporary filename.");
        }
        var classType = findType(temporaryClassName+ ".java", String.format("class %s{ void method(%s p1){ } }", temporaryClassName, fullClassName));
        return classType.flatMap(t -> {
            var methods = t.methods;
            if(methods != null && methods.size() == 1){
                var method = methods.get(0);
                var type = method.paramters.get(0).originalType;
                if(type.getKind() == TypeKind.ERROR){
                    //Nothing
                }else if(type instanceof Type.ClassType){
                    return Optional.of(new ClassType((Type.ClassType) type));
                }else if(type instanceof Type.JCPrimitiveType){
                    return Optional.of(new ClassType((Type.JCPrimitiveType) type));
                }else if(type instanceof Type.JCVoidType){
                    return Optional.of(new ClassType((Type.JCVoidType) type));
                }else if(type instanceof Type.ArrayType){
                    return Optional.of(new ClassType((Type.ArrayType) type));
                }
            }
            return Optional.empty();
        });
    }

    /**
     * Procedure to find out the class type.
     * <ol>
     * <li>Compile the source file</li>
     * <li>Scan the compilation unit tree</li>
     * <li>Get the instance of {@link ClassType}</li>
     * </ol>
     * @param path
     * @return
     */
    private Optional<ClassType> findTypeWithSourceFileObject(SourceFileObject path) {
        CompileResult task = compiler.compile(Set.of(path));
        var foundRoot = task.getCompilationUnitTreeList().stream().filter(root -> root.getSourceFile().toUri().equals(path.toUri())).findFirst();
        if(foundRoot.isPresent()) {
            var builder = new ClassScanner(task.getTrees()).scan(foundRoot.get(), null);
            var info = builder.getClassInfo();
            return Optional.of(info);
        }else
            return Optional.empty();

    }
}