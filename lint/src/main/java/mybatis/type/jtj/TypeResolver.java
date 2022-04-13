package mybatis.type.jtj;

import els.service.TypeFinder;
import els.type.ClassType;
import mybatis.parser.registry.ImmutableTypeAliasRegistry;
import mybatis.project.MyBatisProjectService;

import java.util.Optional;

public class TypeResolver {
    MyBatisProjectService myBatisProjectService;
    TypeFinder finder;
    ImmutableTypeAliasRegistry aliasRegistry = new ImmutableTypeAliasRegistry();

    public TypeResolver(MyBatisProjectService myBatisProjectService) {
        this.myBatisProjectService = myBatisProjectService;
        finder = myBatisProjectService.javaProject.createTypeFinder();
    }

    public Optional<String> resolveAlias(String typeName){
        var typeInRegistry = aliasRegistry.resolveAlias(typeName);
        if(typeInRegistry != null) {
            return Optional.of(typeInRegistry.getName());
        }else{
            return Optional.empty();
        }
    }

    public Optional<String> resolveNameOrAlias(String typeName){
        var typeInRegistry = aliasRegistry.resolveAlias(typeName);
        if(typeInRegistry != null){
            return Optional.of(typeInRegistry.getName());
        }else {
            var optionalClassType = finder.findType(typeName);
            return optionalClassType.map(t -> t.declaredName);
//            if (optionalClassType.isPresent()) {
//                return Optional.of(optionalClassType.get().fullName);
//                builder.withJavaType(javaType);
        }
    }

    public Optional<ClassType> resolveClass(String alias){
        var typeName = this.resolveNameOrAlias(alias);
        if(typeName.isPresent()) {
            return finder.findType(typeName.get());
//            if (optionalClassType.isPresent()) {
//                return optionalClassType;
//                builder.withJavaType(javaType);
//            }
        }
        return Optional.empty();
    }
}
