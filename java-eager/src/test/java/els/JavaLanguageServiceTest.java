package els;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import els.exception.JavaProjectInitializationError;
import els.type.ClassType;
import els.type.MemberMethod;
import els.type.MemberVariable;
import org.apache.ibatis.builder.BaseBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaLanguageServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(JavaLanguageServiceTest.class);

    @BeforeAll
    public static void openSource() throws IOException, URISyntaxException {

    }

//    private static Set<String> searchWorkspace(String query, int limit) {
//        return server.workspaceSymbols(new WorkspaceSymbolParams(query))
//                .stream()
//                .map(result -> result.name)
//                .limit(limit)
//                .collect(Collectors.toSet());
//    }
//
//    private static Set<String> searchFile(URI uri) {
//        return server.documentSymbol(new DocumentSymbolParams(new TextDocumentIdentifier(uri)))
//                .stream()
//                .map(result -> result.name)
//                .collect(Collectors.toSet());
//    }

//    @Test
//    public void all() {
//        var all = searchWorkspace("", 100);
//
////        assertThat(all, not(empty()));
//    }
//
//    @Test
//    public void searchClasses() {
//        var all = searchWorkspace("ABetweenLines", Integer.MAX_VALUE);
//
////        assertThat(all, hasItem("AutocompleteBetweenLines"));
//    }
//
//    @Test
//    public void searchMethods() {
//        var all = searchWorkspace("mStatic", Integer.MAX_VALUE);
//
////        assertThat(all, hasItem("methodStatic"));
//    }
//
//    @Test
//    public void symbolsInFile() {
//        var path = "/org/javacs/example/AutocompleteMemberFixed.java";
//
//        var symbol = server.documentTypeAndSymbol(new DocumentSymbolParams(new TextDocumentIdentifier(FindResource.uri(path))))
//                .stream()
//                .collect(Collectors.toSet());
//        for (var s : symbol) {
//            if(s.typeLocation == null)
//                continue;
//            var list = doGoto(path
//                    , s.typeLocation.range.start.line + 1
//                    , s.typeLocation.range.start.character + 1
//                    , true);
//            System.out.println(list);
//        }
//        assertTrue(symbol == symbol);
//    }

    @Test
    public void findByPath() throws URISyntaxException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var typeFinder = languageService.createJavaProject(root).createTypeFinder();
        var classType1 = typeFinder.findType(root.resolve("./src/main/java/model/Author.java"));
        var classType2 = typeFinder.findType(root.resolve("./src/main/java/model/Blog.java"));
        var classType3 = typeFinder.findType(root.resolve("./src/main/java/model/Content.java"));
        var classType4 = typeFinder.findType(root.resolve("./src/main/java/model/NaverBlog.java"));
        Assertions.assertTrue(classType1.isPresent());
        Assertions.assertTrue(classType2.isPresent());
        Assertions.assertTrue(classType3.isPresent());
        Assertions.assertTrue(classType4.isPresent());
        logger.info(classType1.get().toString());
    }

    /**
     * TODO void type?
     * @throws URISyntaxException
     */
    @Test
    public void findVariousTypes() throws URISyntaxException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var typeFinder = languageService.createJavaProject(root).createTypeFinder();
        var normal = typeFinder.findType("java.util.function.Consumer");
        var primitive = typeFinder.findType("float");
        var parameterized = typeFinder.findType("java.util.List<String>");
        var parameterized2 = typeFinder.findType("java.util.function.Consumer<String>");
        var arrayType = typeFinder.findType("model.Author[]");
        Assertions.assertTrue(normal.isPresent());
        Assertions.assertTrue(primitive.isPresent());
        Assertions.assertTrue(parameterized.isPresent());
        Assertions.assertTrue(parameterized2.isPresent());
        Assertions.assertTrue(arrayType.isPresent());
    }


    @Test
    public void findInUserClasses() throws URISyntaxException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var classFinder = languageService.createJavaProject(root).createTypeFinder();
        var classType1 = classFinder.findType("model.Author");
        var classType2 = classFinder.findType("model.Blog");
        var classType3 = classFinder.findType("model.Content");
        var classType4 = classFinder.findType("model.NaverBlog");
        Assertions.assertTrue(classType1.isPresent());
        Assertions.assertTrue(classType2.isPresent());
        Assertions.assertTrue(classType3.isPresent());
        Assertions.assertTrue(classType4.isPresent());
    }

    @Test
    public void findClassesInJdk() throws URISyntaxException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var classFinder = languageService.createJavaProject(root).createTypeFinder();
        var interfaceType = classFinder.findType("java.util.List");
        var classType = classFinder.findType("java.util.HashSet");
        var classType2 = classFinder.findType("int");
        var classType3 = classFinder.findType("double");
        var classType4 = classFinder.findType("boolean");
        var classType5 = classFinder.findType("float");
        var classType6 = classFinder.findType("java.util.List<String>");
        var classType7 = classFinder.findType("java.lang.Integer");
        var arrayType = classFinder.findType("model.Author[]");
        var arrayType2 = classFinder.findType("byte[]");
        var arrayType3 = classFinder.findType("Byte[]");
        Assertions.assertTrue(interfaceType.isPresent());
        Assertions.assertTrue(classType.isPresent());
        Assertions.assertTrue(classType2.isPresent());
        Assertions.assertTrue(classType3.isPresent());
        Assertions.assertTrue(classType4.isPresent());
        Assertions.assertTrue(classType5.isPresent());
        Assertions.assertTrue(classType6.isPresent());
        Assertions.assertTrue(classType7.isPresent());
        Assertions.assertTrue(arrayType.isPresent());
        Assertions.assertTrue(arrayType2.isPresent());
        Assertions.assertTrue(arrayType3.isPresent());
    }

    @Test
    public void findClassesInDependencies() throws URISyntaxException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var classFinder = languageService.createJavaProject(root).createTypeFinder();
        var interfaceType = classFinder.findType("com.mysql.cj.MysqlConnection");
        var classType = classFinder.findType("org.junit.jupiter.api.Assertions");
        var abstractClassType = classFinder.findType("org.apache.ibatis.builder.BaseBuilder");
        Assertions.assertTrue(interfaceType.isPresent());
        Assertions.assertTrue(classType.isPresent());
        Assertions.assertTrue(abstractClassType.isPresent());
    }

    @Test
    public void compareClassWithReflection() throws URISyntaxException, JavaProjectInitializationError {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("mybatis1").toURI()).normalize();
        var languageService = JavaLanguageService.getLanguageService();
        var classFinder = languageService.createJavaProject(root).createTypeFinder();
        var interfaceType = classFinder.findType("com.mysql.cj.MysqlConnection");
        compareClassWithReflection(interfaceType.get(), MysqlConnection.class);
        var interfaceType2 = classFinder.findType("java.sql.PreparedStatement");
        compareClassWithReflection(interfaceType2.get(), PreparedStatement.class);
        var abstractClassType = classFinder.findType("org.apache.ibatis.builder.BaseBuilder");
        compareClassWithReflection(abstractClassType.get(), BaseBuilder.class);
        var classType = classFinder.findType("org.junit.jupiter.api.Assertions");
        compareClassWithReflection(classType.get(), Assertions.class);
        var classType2 = classFinder.findType("com.mysql.cj.jdbc.ClientPreparedStatement");
        compareClassWithReflection(classType2.get(), ClientPreparedStatement.class);
        var classType3 = classFinder.findType("org.springframework.boot.SpringApplication");
        compareClassWithReflection(classType3.get(), SpringApplication.class);
        var classType4 = classFinder.findType("int");
        compareClassWithReflection(classType4.get(), int.class);
        var classType5 = classFinder.findType("java.util.Collections");
        compareClassWithReflection(classType5.get(), Collections.class);
        var classType6 = classFinder.findType("java.util.Optional");
        compareClassWithReflection(classType6.get(), Optional.class);
        var classType7 = classFinder.findType("java.util.List<String>");
        compareClassWithReflection(classType7.get(), List.class);
    }

    private void compareClassWithReflection(ClassType classType, Class reflectionClass) {
        //Member Variable
        Assertions.assertEquals(reflectionClass.getDeclaredFields().length, classType.fields.size());
        for (var field : reflectionClass.getDeclaredFields()) {
            var memberVariableOpt = findMemberVariable(classType, field);
            Assertions.assertTrue(memberVariableOpt.isPresent(), String.format("%s of %s",field.getName(), classType.toString()));
            var memberVariable = memberVariableOpt.get();
            if (!memberVariable.kind.isPrimitive() && !memberVariable.simpleName.equals("void") && memberVariable.kind != TypeKind.TYPEVAR) {
                Assertions.assertEquals(field.getType().isArray() ? "" : field.getType().getPackageName()
                        , memberVariable.variableType.packageName
                        , String.format("%s of %s", memberVariable.variableType.packageName, memberVariable.variableType.fullName));
                Assertions.assertEquals(field.getType().isArray() ? "Array" : field.getType().getSimpleName()
                        , memberVariable.variableType.simpleName
                        , String.format("%s of %s", memberVariable.variableType.simpleName, memberVariable.variableType.fullName));
            }else
                logger.info("It's skipped to check {} of {} field.", memberVariable.variableType.fullName, memberVariable.name);
        }
        //Member Method
        Assertions.assertEquals(getMethodList(reflectionClass).size(), classType.methods.size());
        for (var method : getMethodList(reflectionClass)) {
            var memberMethodOpt = findMemberMethodByParameterSignature(classType, method);
            Assertions.assertTrue(memberMethodOpt.isPresent(), String.format("%s of %s",method.getName(), classType.toString()));
            var memberMethod = memberMethodOpt.get();
            Assertions.assertEquals(memberMethod.name, method.getName());
            //parameters
            Assertions.assertEquals(method.getParameters().length, memberMethod.paramters.size());
            //return type
            if (memberMethod.returnType.kind != TypeKind.TYPEVAR) {
                Assertions.assertEquals(method.getReturnType().isArray() ? ""
                        : (memberMethod.returnType.kind.isPrimitive() || memberMethod.returnType.simpleName.equals("void") ? "" : method.getReturnType().getPackageName()), memberMethod.returnType.packageName);
                Assertions.assertEquals(method.getReturnType().isArray() ? "Array" : method.getReturnType().getSimpleName(), memberMethod.returnType.simpleName);
            } else{
                logger.info("It's skipped to check {} of {}() method.", memberMethod.returnType.fullName, memberMethod.fullName);
            }
        }
    }

    private List<Method> getMethodList(Class reflectionClass){
        return Arrays.stream(reflectionClass.getDeclaredMethods())
//                .filter(m -> !m.getName().startsWith("lambda$"))
                .collect(Collectors.toList());
    }

    private Optional<MemberMethod> findMemberMethodByParameterSignature(ClassType classType, Method method) {
        var list = classType.methodMap.get(method.getName());
        return list.stream().filter(candidate ->
                candidate.paramters.stream()
                        .map(p -> p.parameterType.flatName).collect(Collectors.joining()).equals(
                                Arrays.stream(method.getGenericParameterTypes())
                                        .map(clazz -> clazz.getTypeName().endsWith("[]") ? "Array"
                                                : clazz.getTypeName().split("<")[0])
                                        .collect(Collectors.joining())
                        )
        ).findFirst();
    }

    private Optional<MemberVariable> findMemberVariable(ClassType classType, Field field) {
        return Optional.ofNullable(classType.fieldMap.get(field.getName()));
    }
//
//    private static JavaLanguageService getJavaLanguageServer(Path workspaceRoot) {
//        return getJavaLanguageServer(workspaceRoot, diagnostic -> logger.info(diagnostic.message));
//    }
//
//    private static JavaLanguageService getJavaLanguageServer(Path workspaceRoot, Consumer<Diagnostic> onError) {
//        return getJavaLanguageServer(
//                workspaceRoot,
//                new LanguageClient() {
//                    @Override
//                    public void publishDiagnostics(PublishDiagnosticsParams params) {
//                        params.diagnostics.forEach(onError);
//                    }
//
//                    @Override
//                    public void showMessage(ShowMessageParams params) {
//                    }
//
//                    @Override
//                    public void registerCapability(String method, JsonElement options) {
//                    }
//
//                    @Override
//                    public void customNotification(String method, JsonElement params) {
//                    }
//                });
//    }
//
//    private static JavaLanguageService getJavaLanguageServer(Path workspaceRoot, LanguageClient client) {
//        var server = new JavaLanguageServiceBuilder(workspaceRoot);
//        return server.createService();
//    }
}
