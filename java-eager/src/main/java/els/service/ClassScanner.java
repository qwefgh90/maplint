package els.service;

import com.sun.source.tree.*;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import els.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Visitor to scan the compilation unit tree.
 */
public class ClassScanner extends TreePathScanner<ClassType.ClassTypeBuilder, Void>{
    Logger logger = LoggerFactory.getLogger(ClassScanner.class);
    private final Trees trees;
    private CompilationUnitTree rootCompilationUnitTree;
    private Container container;
    private String sourceFileName;
    private final ClassType.ClassTypeBuilder builder = ClassType.ClassTypeBuilder.aClassInformation();
//    private Map<String, String> classNameToPackageMap = new HashMap<>();

    /**
     * TODO remove the root parameter
     * @param task
     */
    public ClassScanner(Trees task) {
        this.trees = task;
//        this.rootCompilationUnitTree = root; //task.root();
//        this.sourceFileName = ((JCTree.JCCompilationUnit) root).sourcefile.getName().toString();
    }

    @Override
    public ClassType.ClassTypeBuilder visitCompilationUnit(CompilationUnitTree t, Void v) {
        if(t instanceof JCTree.JCCompilationUnit) {
            this.rootCompilationUnitTree = t; //task.root();
            this.sourceFileName = ((JCTree.JCCompilationUnit) t).sourcefile.getName().toString();
            container = new Container(Objects.toString(t.getPackageName(), "").toString(), t);
            super.visitCompilationUnit(t, v);
            return builder;
        }
        return null;
    }

//    @Override
//    public Void visitImport(ImportTree node, ClassInformation.ClassInformationBuilder classInformationBuilder) {
//        logger.info(node.toString());
//        var identifier = node.getQualifiedIdentifier();
//        if(identifier instanceof JCTree.JCFieldAccess){
//            var packageName = ((JCTree.JCFieldAccess) identifier).selected.toString();
//            var name = ((JCTree.JCFieldAccess) identifier).name.toString();
//            classNameToPackageMap.put(name, packageName);
//        }
//        return super.visitImport(node, classInformationBuilder);
//    }

    @Override
    public ClassType.ClassTypeBuilder visitClass(ClassTree t, Void v) {
        var old = container;
        if(t instanceof JCTree.JCClassDecl && old.container.getKind() == Tree.Kind.COMPILATION_UNIT
        && sourceFileName.contains(t.getSimpleName().toString() + ".java")
        && ((JCTree.JCClassDecl) t).type instanceof Type.ClassType) {
            builder.withClassType((Type.ClassType)((JCTree.JCClassDecl) t).type);
            container = new Container(t.getSimpleName().toString(), t);
            super.visitClass(t, v);
            container = old;
        }
        return null;
    }

    @Override
    public ClassType.ClassTypeBuilder visitMethod(MethodTree t, Void v) {
        if(t.getName().toString().equals("<init>")&& !t.getName().toString().equals("<clinit>")){
            return null;
        }
        if(t instanceof JCTree.JCMethodDecl) {
            var decl = (JCTree.JCMethodDecl)t;
            var info = new MemberMethod(t.getName().toString()
                    , false
                    , getLocation(t)
                    , container.name.toString()
                    , new BaseType(decl.getReturnType().type)
                    , decl.params.stream().map(p ->
                            new Parameter(p.sym, p.getName().toString(), new BaseType(p.type)))
                    .collect(Collectors.toList())
                    , decl.sym
            );
            builder.addMethod(info);
//            var old = containerName;
//            containerName = t.getName();
//            super.visitMethod(t, builder);
//            containerName = old;
        }
        return null;
    }

    @Override
    public ClassType.ClassTypeBuilder visitVariable(VariableTree t, Void v) {
        if(t instanceof JCTree.JCVariableDecl &&
                (((JCTree.JCVariableDecl) t).type instanceof Type.ClassType
            || ((JCTree.JCVariableDecl) t).type instanceof Type.JCPrimitiveType
            || ((JCTree.JCVariableDecl) t).type instanceof Type.ArrayType)
        ){
            JCTree.JCVariableDecl decl = (JCTree.JCVariableDecl)t;
            var info = new MemberVariable(decl.getName().toString(), new BaseType(decl.vartype.type),false
                    , getLocation(decl.getType()), container.name.toString(), decl.sym);
            builder.addField(info);
//            var old = containerName;
//            containerName = t.getName();
//            super.visitVariable(t, builder);
//            containerName = old;
            return null;
        }else{
            return null;
//            return super.visitVariable(t, builder);
        }
    }

    //https://www.javadoc.io/doc/org.kohsuke.sorcerer/sorcerer-javac/latest/com/sun/tools/javac/code/Type.html
    //https://www.javadoc.io/static/org.kohsuke.sorcerer/sorcerer-javac/0.11/com/sun/tools/javac/code/Type.ClassType.html

//    private String asFullyQualifiedName(Tree type){
//        var simpleOrFull = type.toString();
//        var kind = type.getKind();
//        switch (kind){
//            case PARAMETERIZED_TYPE: //JCTypeApply
//                return asFullyQualifiedName(((JCTree.JCTypeApply)type).getType());
//            case IDENTIFIER:
//                if(!simpleOrFull.contains(".")){
//                    var simpleName = simpleOrFull.substring(simpleOrFull.lastIndexOf(".") + 1);
//                    var packageName = classNameToPackageMap.get(simpleName);
//                    if(packageName != null)
//                        return packageName + "." + simpleName;
//                }
//            case PRIMITIVE_TYPE:
//            default:
//                return simpleOrFull;
//        }
//    }

    private static int asSymbolKind(Tree.Kind k) {
        switch (k) {
            case ANNOTATION_TYPE:
            case CLASS:
                return SymbolKind.Class;
            case ENUM:
                return SymbolKind.Enum;
            case INTERFACE:
                return SymbolKind.Interface;
            case METHOD:
                return SymbolKind.Method;
            case TYPE_PARAMETER:
                return SymbolKind.TypeParameter;
            case VARIABLE:
                // This method is used for symbol-search functionality,
                // where we only return fields, not local variables
                return SymbolKind.Field;
            default:
                return 0;
        }
    }

    private Location getLocation(Tree t) {
        var pos = trees.getSourcePositions();
        var lines = rootCompilationUnitTree.getLineMap();
        //TODO when variableTree includes "var", it is converted to java.util.List<E> in CompilationUnit.
        //TODO So, pos.getStartPosition return -1
        var start = pos.getStartPosition(rootCompilationUnitTree, t);
        var end = pos.getEndPosition(rootCompilationUnitTree, t);
        var startLine = (int) lines.getLineNumber(start);
        var startColumn = (int) lines.getColumnNumber(start);
        var endLine = (int) lines.getLineNumber(end);
        var endColumn = (int) lines.getColumnNumber(end);
        var range = new Range(new Position(startLine - 1, startColumn - 1), new Position(endLine - 1, endColumn - 1));
        return new Location(rootCompilationUnitTree.getSourceFile().toUri(), range);
    }
//    private Location location(Tree t) {
//        logger.info(t.toString());
//        if(t.toString().contains("java.util.List<E>")){
//            logger.info("this");
//        }
//        var trees = Trees.instance(task.task);
//        var pos = trees.getSourcePositions();
//        var lines = task.root().getLineMap();
//        var start = pos.getStartPosition(root, t);
//        var end = pos.getEndPosition(root, t);
//        var startLine = (int) lines.getLineNumber(start);
//        var startColumn = (int) lines.getColumnNumber(start);
//        var endLine = (int) lines.getLineNumber(end);
//        var endColumn = (int) lines.getColumnNumber(end);
//        var range = new Range(new Position(startLine - 1, startColumn - 1), new Position(endLine - 1, endColumn - 1));
//        return new Location(root.getSourceFile().toUri(), range);
//    }

    private static class Container{
        private String name;
        private Tree container;
        Container(String name, Tree container) {
            this.name = name;
            this.container = container;
        }
    }
}
