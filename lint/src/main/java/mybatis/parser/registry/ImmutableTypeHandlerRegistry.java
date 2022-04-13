package mybatis.parser.registry;

import org.apache.ibatis.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class ImmutableTypeHandlerRegistry {
    Logger logger = LoggerFactory.getLogger(ImmutableTypeHandlerRegistry.class);
    public final static ImmutableTypeHandlerRegistry immutableTypeHandlerRegistry = new ImmutableTypeHandlerRegistry();
    private final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    public TypeHandler<?> getMappingTypeHandler(Class<? extends TypeHandler<?>> handlerType) {
        return typeHandlerRegistry.getMappingTypeHandler(handlerType);
    }

//    public <T> TypeHandler<T> getTypeHandler(String type) {
//        return typeHandlerRegistry.getTypeHandler(type);
//    }

    public <T> TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference) {
        return typeHandlerRegistry.getTypeHandler(javaTypeReference);
    }

    public TypeHandler<?> getTypeHandler(JdbcType jdbcType) {
        return typeHandlerRegistry.getTypeHandler(jdbcType);

    }

    public TypeHandler getTypeHandler(Class type, JdbcType jdbcType) {
        return typeHandlerRegistry.getTypeHandler(type, jdbcType);
    }
    public TypeHandler getTypeHandler(String type, JdbcType jdbcType) {
        try {
            var clazz = org.apache.commons.lang3.ClassUtils.getClass(type);
//            var clazz = Class.forName(type);
            return getTypeHandler(clazz, jdbcType);
        } catch (ClassNotFoundException e) {
            logger.trace(type + " is not resolved.", e);
        }
        return null;
    }

    public boolean hasTypeHandler(String type) {
        try {
            var clazz = org.apache.commons.lang3.ClassUtils.getClass(type);
            return hasTypeHandler(clazz);
        } catch (ClassNotFoundException e) {
            logger.trace(type + " is not resolved.", e);
        }
        return false;
    }

    public boolean hasTypeHandler(Class<?> javaType) {
        return typeHandlerRegistry.hasTypeHandler(javaType, null);
    }

    public boolean hasTypeHandler(TypeReference<?> javaTypeReference) {
        return typeHandlerRegistry.hasTypeHandler(javaTypeReference, null);
    }

    public boolean hasTypeHandler(TypeReference<?> javaTypeReference, JdbcType jdbcType) {
        return javaTypeReference != null && typeHandlerRegistry.getTypeHandler(javaTypeReference, jdbcType) != null;
    }

    public TypeHandler<Object> getUnknownTypeHandler() {
        return typeHandlerRegistry.getUnknownTypeHandler();
    }

    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }

}
