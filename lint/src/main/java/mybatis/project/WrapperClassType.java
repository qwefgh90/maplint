package mybatis.project;

import els.type.*;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.util.MapUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sun.tools.javac.code.Type;

/**
 * Additional data structures are included for Setter and Getter
 * Based on MetaClass.java and Reflector.java
 */
public class WrapperClassType {
    private final ClassType classType;
    private final Map<String, Member> getMethods = new HashMap<>();
    private final Map<String, Member> setMethods = new HashMap<>();
    private final Map<String, BaseType> setTypes = new HashMap<>();
    private final Map<String, BaseType> getTypes = new HashMap<>();

    private WrapperClassType(ClassType classType) {
        this.classType = classType;
        addGetMethods(getClassMethods(this.classType));
        addSetMethods(getClassMethods(this.classType));
        addFields(this.classType);
    }

    private List<MemberMethod> getClassMethods(ClassType clazz) {
        Map<String, MemberMethod> uniqueMethods = new HashMap<>();
        var currentClass = clazz;
        while (currentClass != null
                && !currentClass.originalType.tsym.getQualifiedName().equals(Object.class.getName())) {
            addUniqueMethods(uniqueMethods, currentClass.methods);

            // we also need to look for interface methods -
            // because the class may be abstract
//            var interfaces = ((Type.ClassType)currentClass.originalType).interfaces_field;
            var interfaces = currentClass.getInterfaceTypes();
            for (ClassType anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.methods);
            }
            currentClass = currentClass.getSuperType();
//            var superType = ((Type.ClassType) currentClass.originalType).supertype_field;
//            if(superType instanceof Type.ClassType){
//                currentClass = ClassType.ClassTypeBuilder
//                        .aClassInformation()
//                        .withClassType((Type.ClassType) superType)
//                        .getClassInfo();
//            }else
//                currentClass = null;
        }

        List<MemberMethod> methods = uniqueMethods.values().stream().collect(Collectors.toList());
        return methods;
    }

    private void addUniqueMethods(Map<String, MemberMethod> uniqueMethods, List<MemberMethod> methods) {
        for (MemberMethod currentMethod : methods) {
//            if (!currentMethod.isBridge()) {
            //TODO
            String signature = getSignature(currentMethod);
            // check to see if the method is already known
            // if it is known, then an extended class must have
            // overridden a method
            if (!uniqueMethods.containsKey(signature)) {
                uniqueMethods.put(signature, currentMethod);
            }
//            }
        }
    }

    private String getSignature(MemberMethod method) {
        StringBuilder sb = new StringBuilder();
        var returnType = method.returnType;
        if (returnType != null) {
            sb.append(returnType.declaredName).append('#');
        }
        sb.append(method.name);
        var parameters = method.paramters;
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(i == 0 ? ':' : ',').append(parameters.get(i).name);
        }
        return sb.toString();
    }

    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (setMethods.containsKey(prop.getName())) {
                var metaProp = metaClassForProperty(prop.getName());
                return metaProp.hasSetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return setMethods.containsKey(prop.getName());
        }
    }

    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (getMethods.containsKey(prop.getName())) {
                var metaProp = metaClassForProperty(prop.getName());
                return metaProp.hasGetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return getMethods.containsKey(prop.getName());
        }
    }

    public BaseType getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            var metaProp = metaClassForProperty(prop);
            return metaProp.getGetterType(prop.getChildren());
        }
        // issue #506. Resolve the type inside a Collection Object
        return getGetterType(prop);
    }


    public BaseType getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            var metaProp = metaClassForProperty(prop.getName());
            return metaProp.getSetterType(prop.getChildren());
        } else {
            return setTypes.get(prop.getName());
        }
    }

    private void addSetMethods(List<MemberMethod> methods) {
        Map<String, List<MemberMethod>> conflictingSetters = new HashMap<>();
        methods.stream().filter(m -> m.paramters.size() == 1 && PropertyNamer.isSetter(m.name))
                .forEach(m -> addMethodConflict(conflictingSetters, PropertyNamer.methodToProperty(m.name), m));
        resolveSetterConflicts(conflictingSetters);
    }

    private void addGetMethods(List<MemberMethod> methods) {
        Map<String, List<MemberMethod>> conflictingGetters = new HashMap<>();
        methods.stream().filter(m -> m.paramters.size() == 0 && PropertyNamer.isGetter(m.name))
                .forEach(m -> addMethodConflict(conflictingGetters, PropertyNamer.methodToProperty(m.name), m));
        resolveGetterConflicts(conflictingGetters);
    }

    private void addFields(ClassType classType) {
        var fields = classType.fields;
        for (MemberVariable field : fields) {
            if (!setMethods.containsKey(field.name)) {
                // issue #379 - removed the check for final because JDK 1.5 allows
                // modification of final fields through reflection (JSR-133). (JGB)
                // pr #16 - final static can only be set by the classloader
                if (!(field.originalType.isFinal() && field.originalType.tsym.isStatic())) {
                    addSetField(field);
                }
            }
            if (!getMethods.containsKey(field.name)) {
                addGetField(field);
            }
        }

        var superType = classType.getSuperType();
        if (superType instanceof ClassType) {
            var superClassType = new WrapperClassType(superType);
            addFields(superClassType.classType);
        }
    }

    public static WrapperClassType create(Type.ClassType classType) {
        if (classType != null) {
            var superClassType = new ClassType(classType);
            return new WrapperClassType(superClassType);
        }
        return null;
    }

    public static WrapperClassType create(ClassType classType) {
        if (classType != null) {
            return new WrapperClassType(classType);
        }
        return null;
    }

    private void resolveSetterConflicts(Map<String, List<MemberMethod>> conflictingSetters) {
        for (Map.Entry<String, List<MemberMethod>> entry : conflictingSetters.entrySet()) {
            String propName = entry.getKey();
            List<MemberMethod> setters = entry.getValue();
            BaseType getterType = getTypes.get(propName);
            boolean isGetterAmbiguous = getMethods.get(propName) instanceof AmbiguousMemberMethod;
            boolean isSetterAmbiguous = false;
            MemberMethod match = null;
            for (MemberMethod setter : setters) {
                if (!isGetterAmbiguous
                        && setter.paramters.get(0).originalType.tsym.getQualifiedName().toString()
                        .equals(getterType.originalType.tsym.getQualifiedName().toString())) {
                    // should be the best match
                    match = setter;
                    break;
                }
                if (!isSetterAmbiguous) {
                    match = pickBetterSetter(match, setter, propName);
                    isSetterAmbiguous = match == null;
                }
            }
            if (match != null) {
                addSetMethod(propName, match);
            }
        }
    }

    private MemberMethod pickBetterSetter(MemberMethod setter1, MemberMethod setter2, String property) {
        if (setter1 == null) {
            return setter2;
        }
        BaseType paramType1 = setter1.paramters.get(0);
        BaseType paramType2 = setter2.paramters.get(0);

        if (isAssignableFrom(
                (Type.ClassType) paramType1.originalType
                , (Type.ClassType) paramType2.originalType)) {
            return setter2;
        } else if (isAssignableFrom(
                (Type.ClassType) paramType2.originalType
                , (Type.ClassType) paramType1.originalType)) {
            return setter1;
        }
        MemberMethod method = new AmbiguousMemberMethod(setter1);
        setMethods.put(property, method);
        setTypes.put(property, setter1.paramters.get(0).parameterType);
        return null;
    }

    private void addSetMethod(String name, MemberMethod method) {
        setMethods.put(name, method);
        setTypes.put(name, method.paramters.get(0).parameterType);
    }

    private void resolveGetterConflicts(Map<String, List<MemberMethod>> conflictingGetters) {
        for (Map.Entry<String, List<MemberMethod>> entry : conflictingGetters.entrySet()) {
            MemberMethod winner = null;
            String propName = entry.getKey();
            boolean isAmbiguous = false;
            for (MemberMethod candidate : entry.getValue()) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }
                BaseType winnerType = winner.returnType;
                BaseType candidateType = candidate.returnType;
                if (candidateType.equals(winnerType)) {
                    if (!boolean.class.getName().equals(candidateType.declaredName)) {
                        isAmbiguous = true;
                        break;
                    } else if (candidate.name.startsWith("is")) {
                        winner = candidate;
                    }
                } else {
                    if (isAssignableFrom((Type.ClassType) candidateType.originalType, (Type.ClassType) winnerType.originalType)) {
                        // OK getter type is descendant
                    } else if (isAssignableFrom((Type.ClassType) winnerType.originalType, (Type.ClassType) candidateType.originalType)) {
                        winner = candidate;
                    } else {
                        isAmbiguous = true;
                        break;
                    }
                }
            }
            addGetMethod(propName, winner, isAmbiguous);
        }
    }

    private void addSetField(MemberVariable field) {
        if (isValidPropertyName(field.name)) {
            setMethods.put(field.name, field);
            setTypes.put(field.name, field.variableType);
        }
    }

    private void addGetField(MemberVariable field) {
        if (isValidPropertyName(field.name)) {
            getMethods.put(field.name, field);
            getTypes.put(field.name, field.variableType);
        }
    }

    public static boolean isAssignableFrom(Type.ClassType type, Type.ClassType from) {
        type = (Type.ClassType) type.tsym.type;
        return isAssignableFrom(type.tsym.getQualifiedName().toString(), from);
    }

    public static boolean isAssignableFrom(String type, Type.ClassType from) {
        from = (Type.ClassType) from.tsym.type;
        if (type
                .equals(from.tsym.getQualifiedName().toString())) {
            return true;
        }
        Function<Type.ClassType, List<Type.ClassType>> getParents = (Type.ClassType classType) -> {
            List<Type.ClassType> parentCandidates = new ArrayList<>();
            if (classType.supertype_field instanceof Type.ClassType)
                parentCandidates.add((Type.ClassType) classType.supertype_field);
            if (classType.interfaces_field != null)
                classType.interfaces_field.forEach(interfaceField -> parentCandidates.add((Type.ClassType) interfaceField));
            return parentCandidates;
        };
        var parents = getParents.apply(from);
        for (var parent : parents) {
            var test = isAssignableFrom(type, parent);
            if (test) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignableFrom(String type, Type from) {
        if (from instanceof Type.ClassType) {
            return isAssignableFrom(type, (Type.ClassType) from);
        } else if (from instanceof Type.ArrayType) {
            return isAssignableFrom(type, (Type.ClassType)(((Type.ArrayType) from).tsym.type));
        }
        return false;
    }

    private void addMethodConflict(Map<String, List<MemberMethod>> conflictingMethods, String name, MemberMethod method) {
        if (isValidPropertyName(name)) {
            List<MemberMethod> list = MapUtil.computeIfAbsent(conflictingMethods, name, k -> new ArrayList<>());
            list.add(method);
        }
    }

    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    private void addGetMethod(String name, MemberMethod method, boolean isAmbiguous) {
        MemberMethod member = isAmbiguous
                ? new AmbiguousMemberMethod(method)
                : method;
        getMethods.put(name, member);
        getTypes.put(name, member.returnType);
    }

    private WrapperClassType metaClassForProperty(String name) {
        var type = getTypes.get(name);
        if (type.originalType instanceof Type.ClassType) {
            return create((Type.ClassType) type.originalType);
        }
        return null;
    }

    private WrapperClassType metaClassForProperty(PropertyTokenizer prop) {
        var propType = getGetterType(prop);
        if (propType.originalType instanceof Type.ClassType) {
            return create((Type.ClassType) propType.originalType);
        }
        return null;
    }

    private BaseType getGetterType(PropertyTokenizer prop) {
        var type = getTypes.get(prop.getName());
        if (prop.getIndex() != null
                && isAssignableFrom(Collection.class.getName(), (Type.ClassType) type.originalType)) {
            var returnTypeMaybeNull = getGenericGetterType(prop.getName());
            if (returnTypeMaybeNull != null && returnTypeMaybeNull.originalType.isParameterized()) {
                var returnType = returnTypeMaybeNull.originalType;
                var actualTypeArguments = returnType.getTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.size() == 1) {
                    returnType = actualTypeArguments.get(0);
                    if (returnType instanceof Type.ClassType) {
                        return new BaseType(returnType);
                    } else if (returnType.isParameterized()) {
                        return new BaseType(returnType);
                    }
                }
            }
        }
//        return new BaseType(type.originalSymbol);
        return type;
    }

    private BaseType getGenericGetterType(String propertyName) {
        Member member = getMethods.get(propertyName);
        if (member instanceof MemberMethod) {
//                Field declaredMethod = MethodInvoker.class.getDeclaredField("method");
//                declaredMethod.setAccessible(true);
//                Method method = (Method) declaredMethod.get(invoker);
            return ((MemberMethod) member).returnType;
        } else if (member instanceof MemberVariable) {
//                Field declaredField = GetFieldInvoker.class.getDeclaredField("field");
//                declaredField.setAccessible(true);
//                Field field = (Field) declaredField.get(member);
            return member;
        }
        return null;
    }
}
