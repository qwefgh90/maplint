package mybatis.parser.sql.bound;

import com.sun.tools.javac.code.Type;
import mybatis.parser.BaseParser;
import mybatis.parser.GenericTokenParser;
import mybatis.parser.TokenHandler;
import mybatis.parser.model.Config;
import mybatis.parser.model.NotationToken;
import mybatis.parser.model.ParameterMapChild;
import mybatis.parser.model.ResolvingType;
import mybatis.parser.registry.ImmutableTypeHandlerRegistry;
import mybatis.project.ClassTypeWrapper;
import org.apache.ibatis.builder.*;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * It's forked from SqlSourceBuilder.
 */
public class BoundSqlStatementSourceBuilder extends BaseParser {
    private static final String PARAMETER_PROPERTIES = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

    public BoundSqlStatementSourceBuilder(Config configuration) {
        super(configuration);
    }

    public StaticBoundSqlStatementSource build(String originalSql, String parameterType, Map<String, Object> additionalParameters) {
        BoundSqlStatementSourceBuilder.ParameterMappingTokenHandler handler = new BoundSqlStatementSourceBuilder.ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql;
        if (configuration.isShrinkWhitespacesInSql()) {
            sql = parser.parse(removeExtraWhitespaces(originalSql));
        } else {
            sql = parser.parse(originalSql);
        }
        sql = sql + String.format("\n--%s", String.join(",", parser.getUniqueIdList())); // add a list of unique ids at the last line.
        return new StaticBoundSqlStatementSource(configuration, sql, handler.getParameterMappings());
    }

    public static String removeExtraWhitespaces(String original) {
        StringTokenizer tokenizer = new StringTokenizer(original);
        StringBuilder builder = new StringBuilder();
        boolean hasMoreTokens = tokenizer.hasMoreTokens();
        while (hasMoreTokens) {
            builder.append(tokenizer.nextToken());
            hasMoreTokens = tokenizer.hasMoreTokens();
            if (hasMoreTokens) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    private static class ParameterMappingTokenHandler extends BaseParser implements TokenHandler {

        Logger logger = LoggerFactory.getLogger(ParameterMappingTokenHandler.class);
        private final List<ParameterMapChild> parameterMappings = new ArrayList<>();
        private final String parameterType;

        public ParameterMappingTokenHandler(Config configuration, String parameterType, Map<String, Object> additionalParameters) {
            super(configuration);
            this.parameterType = parameterType;
//            this.signature = signature;
//            this.metaParameters = configuration.newMetaObject(additionalParameters);
        }

        public List<ParameterMapChild> getParameterMappings() {
            return parameterMappings;
        }

        @Override
        public String handleToken(String content, NotationToken notation, int index, List<String> uniqueIdList, String statement) {
            var child = buildParameterMapping(content, notation, index);
            parameterMappings.add(child);
            try {
                if (child.getJavaType().equals(Integer.class.getName())
                        || child.getJavaType().equals(Long.class.getName())
                        || child.getJavaType().equals(Short.class.getName())
                        || child.getJavaType().equals(Boolean.class.getName())
                        || child.getJavaType().equals(int.class.getName())
                        || child.getJavaType().equals(long.class.getName())
                        || child.getJavaType().equals(short.class.getName())
                        || child.getJavaType().equals(boolean.class.getName())
                        || child.getJavaType().equals(BigInteger.class.getName())
                        || child.getJavaType().equals(BigDecimal.class.getName())
                ) {
                    //TODO: It does not support the number
                    var id = generateUniqueId(notation.getToken().toString().length() - 2, uniqueIdList, statement);
                    uniqueIdList.add(id);
                    return String.format("'%s'", id);
                } else {
                    var id = generateUniqueId(notation.getToken().toString().length() - 2, uniqueIdList, statement);
                    uniqueIdList.add(id);
                    return String.format("'%s'", id);
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("An exception occurs while handling %s", notation.getToken().toLowerCase(Locale.ROOT)));
            }
        }

        private String generateUniqueId(int idLength, List<String> reservedList, String body) throws Exception {
            int tryCount = 0;
            while (tryCount < 100) {
                var candidate = getIdBiggerThan(idLength).substring(0, idLength);
                var foundInReservedList = reservedList.stream().filter(s -> s.equals(candidate)).findFirst().isPresent();
                var foundInBody = body.contains(candidate);
                if (!foundInBody && !foundInReservedList)
                    return candidate;
                tryCount++;
            }
            throw new Exception("It failed to generate unique id.");
        }

        private String getIdBiggerThan(int count) {
            String candidate = "";
            var length = candidate.length();
            while (length < count) {
                candidate += UUID.randomUUID().toString();
                length = candidate.length();
            }
            return candidate;
        }

        /**
         * javaType is null when it is not resolved.
         *
         * @param content  java property
         * @param notation notation in MappedSQLStatement
         * @param index
         * @return
         */
        private ParameterMapChild buildParameterMapping(String content, NotationToken notation, int index) {
            Map<String, String> propertiesMap = parseParameterMapping(content);
            String property = propertiesMap.get("property");
            String propertyType = null;
            ResolvingType resolvingType;
            var parameterTypeClass = configuration
                    .getTypeResolver()
                    .resolveClass(parameterType);
//            if (metaParameters.hasGetter(property)) { // issue #448 get type from additional params
//                propertyType = metaParameters.getGetterType(property);
//            } else
            if (//configuration.getTypeResolver().resolveAlias(parameterType).isPresent()
                    parameterTypeClass.isPresent() &&
                            ImmutableTypeHandlerRegistry.immutableTypeHandlerRegistry.hasTypeHandler(
                                    parameterTypeClass.get().fullName)
            ) {
                // If parameterType is the type like primitive
                // which has a typeHandler (int, long, ...)
                resolvingType = ResolvingType.RegisteredParameterTypeFound;
                propertyType = parameterTypeClass.get().fullName;
            } else if (JdbcType.CURSOR.name().equals(propertiesMap.get("jdbcType"))) {
                resolvingType = ResolvingType.CursorPropertyTypeFound;
                propertyType = java.sql.ResultSet.class.getName();
            } else if (property == null) {
                resolvingType = ResolvingType.PropertyNameNotFound;
                propertyType = Object.class.getName();
            } else if (parameterTypeClass.isPresent()
                    && ClassTypeWrapper.isAssignableFrom(Map.class.getName()
                    , parameterTypeClass.get().originalType)) {
                resolvingType = ResolvingType.MapParameterTypeFound;
                propertyType = Object.class.getName();
            } else if (parameterTypeClass.isPresent() && parameterTypeClass.get().originalType instanceof Type.ClassType) {
                var wrapperClassType = ClassTypeWrapper.create(parameterTypeClass.get());
                if (wrapperClassType.hasGetter(property)) {
                    resolvingType = ResolvingType.GetterFound;
                    propertyType = wrapperClassType.getGetterType(property).declaredName;
                } else {
                    resolvingType = ResolvingType.GetterNotFound;
                    propertyType = Object.class.getName();
                }
            } else {
                resolvingType = ResolvingType.Unresolved;
                propertyType = Object.class.getName();
            }
            ParameterMapChild.ParameterMapChildBuilder builder = ParameterMapChild.ParameterMapChildBuilder.aParameterMapChild();
            builder.withConfiguration(configuration);
            builder.withProperty(property);
            builder.withJavaType(propertyType);
            builder.withIndex(index);
            builder.withResolvingType(resolvingType);
            builder.withNotation(notation);

            String javaType = propertyType;
            String typeHandlerAlias = null;
            for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if ("javaType".equals(name)) {
                    var optionalClassType = configuration.getTypeResolver().resolveNameOrAlias(value);
                    if (optionalClassType.isPresent()) {
                        javaType = optionalClassType.get();
                        builder.withJavaType(javaType);
                    }
                } else if ("jdbcType".equals(name)) {
                    builder.withJdbcType(resolveJdbcType(value));
                } else if ("mode".equals(name)) {
                    builder.withMode(resolveParameterMode(value));
                } else if ("numericScale".equals(name)) {
                    builder.withNumericScale(Integer.valueOf(value));
                } else if ("resultMap".equals(name)) {
                    builder.withResultMapId(value);
                } else if ("typeHandler".equals(name)) {
                    typeHandlerAlias = value;
                } else if ("jdbcTypeName".equals(name)) {
                    builder.withJdbcTypeName(value);
                } else if ("property".equals(name)) {
                    // Do Nothing
                } else if ("expression".equals(name)) {
                    throw new BuilderException("Expression based parameters are not supported yet");
                } else {
                    throw new BuilderException("An invalid property '" + name + "' was found in mapping #{" + content + "}.  Valid properties are " + PARAMETER_PROPERTIES);
                }
            }
            if (typeHandlerAlias != null) {
                builder.withTypeHandlerType(resolveTypeHandler(javaType, typeHandlerAlias));
            }
            return builder.build();
        }

        private Map<String, String> parseParameterMapping(String content) {
            try {
                return new ParameterExpression(content);
            } catch (BuilderException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BuilderException("Parsing error was found in mapping #{" + content + "}.  Check syntax #{property|(expression), var1=value1, var2=value2, ...} ", ex);
            }
        }
    }

}
