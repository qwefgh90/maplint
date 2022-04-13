package els.type;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO Support all types in {@link https://javadoc.io/static/io.earcam.wrapped/jdk.compiler/1.8.132/com/sun/tools/javac/code/package-summary.html}
 */
public class ClassType extends BaseType {
    private static final Logger logger = LoggerFactory.getLogger(ClassType.class);
    private ClassType(Type classType, List<MemberMethod> methods, List<MemberVariable> fields) {
        super(classType);
        this.methods = Collections.unmodifiableList(methods);
        this.fields = Collections.unmodifiableList(fields);
        var fieldMap = new HashMap<String, MemberVariable>();
        var methodMap = new HashMap<String, HashSet<MemberMethod>>();
        fields.forEach(f -> fieldMap.put(f.name, f));
        methods.forEach(m -> {
            var value = methodMap.computeIfAbsent(m.name, (name) -> new HashSet<>());
            value.add(m);
        });
        this.fieldMap = Collections.unmodifiableMap(fieldMap);
        this.methodMap = Collections.unmodifiableMap(methodMap);
    }

    public ClassType(Type.ClassType classType) {
        this(classType, classType.getKind());
    }

    public ClassType(Type.JCPrimitiveType primitiveType) {
        this(primitiveType, primitiveType.getKind());
    }

    public ClassType(Type.JCVoidType voidType) {
        this(voidType, voidType.getKind());
    }

    public ClassType(Type.ArrayType arrayType) {
        this(arrayType, arrayType.getKind());
    }

    private ClassType(Type type, TypeKind kind) {
        super(type);
        List<MemberMethod> methods = new ArrayList<>();
        List<MemberVariable> fields = new ArrayList<>();
        if(type.tsym.members() != null) {
            for (var symbol : type.tsym.members().getSymbols()) {
                if (symbol instanceof Symbol.MethodSymbol && !symbol.name.toString().equals("<init>") && !symbol.name.toString().equals("<clinit>")) {
                    var info = new MemberMethod(symbol.name.toString()
                            , false
                            , null//location(t)
                            , symbol.owner.name.toString()//container.name.toString()
                            , new BaseType(((Symbol.MethodSymbol) symbol).getReturnType().baseType())//decl.getReturnType().type.tsym)
                            , ((Symbol.MethodSymbol) symbol).params != null ? ((Symbol.MethodSymbol) symbol).params.stream().map(p ->
                                    new Parameter(p.baseSymbol(), p.name.toString(), new BaseType(p.type))).collect(Collectors.toList())
                            : Collections.emptyList()

//                            , (Symbol.MethodSymbol) symbol //decl.sym
                            ,symbol
                    );
                    methods.add(info);
                } else if (symbol instanceof Symbol.VarSymbol) {
                    var info = new MemberVariable(symbol.name.toString()
                            , new BaseType(symbol.type)
                            , false
                            , null, symbol.owner.name.toString(), symbol);
                    fields.add(info);
                }
            }
        }
        this.methods = Collections.unmodifiableList(methods);
        this.fields = Collections.unmodifiableList(fields);
        var fieldMap = new HashMap<String, MemberVariable>();
        var methodMap = new HashMap<String, HashSet<MemberMethod>>();
        fields.forEach(f -> fieldMap.put(f.name, f));
        methods.forEach(m -> {
            var value = methodMap.computeIfAbsent(m.name, (name) -> new HashSet<>());
            value.add(m);
        });
        this.fieldMap = Collections.unmodifiableMap(fieldMap);
        this.methodMap = Collections.unmodifiableMap(methodMap);
    }

    public final List<MemberMethod> methods;
    public final List<MemberVariable> fields;
    public final Map<String, HashSet<MemberMethod>> methodMap;
    public final Map<String, MemberVariable> fieldMap;

    public ClassType getSuperType(){
        if(((Type.ClassType)this.originalType).supertype_field instanceof Type.ClassType)
            return new ClassType((Type.ClassType)((Type.ClassType)this.originalType).supertype_field);
        return null;
    }
    public List<ClassType> getInterfaceTypes(){
        if(((Type.ClassType)this.originalType).interfaces_field != null){
            return ((Type.ClassType)this.originalType).interfaces_field.map(i -> {
                return new ClassType((Type.ClassType)i);
            });
        }
        return null;
    }

    @Override
    public String toString() {
        return "ClassType{" +
                "originalType=" + originalType +
                ", originalSymbol=" + originalSymbol +
                ", packageName='" + packageName + '\'' +
                ", owner='" + owner + '\'' +
                ", simpleName='" + simpleName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", flatName='" + flatName + '\'' +
                ", declaredName='" + declaredName + '\'' +
                ", kind=" + kind +
                ", methods=" + methods +
                ", fields=" + fields +
                ", methodMap=" + methodMap +
                ", fieldMap=" + fieldMap +
                '}';
    }

    public static final class ClassTypeBuilder {
        public Type.ClassType classType;
        public Set<MemberMethod> methodSet = new HashSet<>();
        public Set<MemberVariable> fieldSet = new HashSet<>();

        private ClassTypeBuilder() {
        }

        public static ClassTypeBuilder aClassInformation() {
            return new ClassTypeBuilder();
        }

        public ClassTypeBuilder withClassType(Type.ClassType classType) {
            this.classType = classType;
            return this;
        }

        public void addMethod(MemberMethod m) {
            this.methodSet.add(m);
        }

        public void addField(MemberVariable m) {
            this.fieldSet.add(m);
        }

        public ClassType getClassInfo() {
            if(classType == null)
                throw new RuntimeException("A classType parameter must be passed.");
            return new ClassType(classType, methodSet.stream().collect(Collectors.toList()), fieldSet.stream().collect(Collectors.toList()));
        }
    }
}

