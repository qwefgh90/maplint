package els.compiler.internal;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompileResult {
    private final CompileParameter compileParameter;
    private final List<CompilationUnitTree> compilationUnitTreeList;
    private final Trees trees;
    private final Elements elements;
    private final Types types;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;
    private final Set<Path> unresolvedResources;

    public CompileResult(CompileParameter compileParameter, List<CompilationUnitTree> compilationUnitTreeList, Trees trees, Elements elements, Types types, List<Diagnostic<? extends JavaFileObject>> diagnostics, Set<Path> unresolvedResources) {
        this.compilationUnitTreeList = compilationUnitTreeList;
        this.trees = trees;
        this.elements = elements;
        this.types = types;
        this.diagnostics = diagnostics;
        this.unresolvedResources = unresolvedResources;
        this.compileParameter = compileParameter;
    }

    public CompileParameter getCompileParameter() {
        return compileParameter;
    }

    public List<CompilationUnitTree> getCompilationUnitTreeList() {
        return compilationUnitTreeList;
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

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }

    public Set<Path> getUnresolvedResources() {
        return unresolvedResources;
    }
}
