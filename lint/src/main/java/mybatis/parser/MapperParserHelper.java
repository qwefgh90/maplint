package mybatis.parser;

import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.model.ParameterMapChild;
import mybatis.parser.model.ResultMapChild;
import mybatis.parser.sql.bound.BoundSqlStatementSource;
import mybatis.project.ClassTypeWrapper;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * it's forked from MapperBuilderAssistant
 */
public class MapperParserHelper extends BaseParser{
    Logger logger = LoggerFactory.getLogger(MapperParserHelper.class);
    private String currentNamespace;
    private final String resource;
//    private Cache currentCache;
    private boolean unresolvedCacheRef; // issue #676

    public MapperParserHelper(Config configuration, String resource) {
        super(configuration);
        ErrorContext.instance().resource(resource);
        this.resource = resource;
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        if (currentNamespace == null) {
            throw new BuilderException("The mapper element requires a namespace attribute to be specified.");
        }

        if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
            throw new BuilderException("Wrong namespace. Expected '"
                    + this.currentNamespace + "' but found '" + currentNamespace + "'.");
        }

        this.currentNamespace = currentNamespace;
    }

    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }
        if (isReference) {
            // is it qualified with any namespace yet?
            if (base.contains(".")) {
                return base;
            }
        } else {
            // is it qualified with this namespace yet?
            if (base.startsWith(currentNamespace + ".")) {
                return base;
            }
            if (base.contains(".")) {
                throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
            }
        }
        return currentNamespace + "." + base;
    }

//    public Cache useCacheRef(String namespace) {
//        if (namespace == null) {
//            throw new BuilderException("cache-ref element requires a namespace attribute.");
//        }
//        try {
//            unresolvedCacheRef = true;
//            Cache cache = configuration.getCache(namespace);
//            if (cache == null) {
//                throw new IncompleteElementException("No cache for namespace '" + namespace + "' could be found.");
//            }
//            currentCache = cache;
//            unresolvedCacheRef = false;
//            return cache;
//        } catch (IllegalArgumentException e) {
//            throw new IncompleteElementException("No cache for namespace '" + namespace + "' could be found.", e);
//        }
//    }
//
//    public Cache useNewCache(Class<? extends Cache> typeClass,
//                             Class<? extends Cache> evictionClass,
//                             Long flushInterval,
//                             Integer size,
//                             boolean readWrite,
//                             boolean blocking,
//                             Properties props) {
//        Cache cache = new CacheBuilder(currentNamespace)
//                .implementation(valueOrDefault(typeClass, PerpetualCache.class))
//                .addDecorator(valueOrDefault(evictionClass, LruCache.class))
//                .clearInterval(flushInterval)
//                .size(size)
//                .readWrite(readWrite)
//                .blocking(blocking)
//                .properties(props)
//                .build();
//        configuration.addCache(cache);
//        currentCache = cache;
//        return cache;
//    }
//
//    public ParameterMap addParameterMap(String id, Class<?> parameterClass, List<ParameterMapping> parameterMappings) {
//        id = applyCurrentNamespace(id, false);
//        ParameterMap parameterMap = new ParameterMap.Builder(configuration, id, parameterClass, parameterMappings).build();
//        configuration.addParameterMap(parameterMap);
//        return parameterMap;
//    }
//
//    public ParameterMapping buildParameterMapping(
//            Class<?> parameterType,
//            String property,
//            Class<?> javaType,
//            JdbcType jdbcType,
//            String resultMap,
//            ParameterMode parameterMode,
//            Class<? extends TypeHandler<?>> typeHandler,
//            Integer numericScale) {
//        resultMap = applyCurrentNamespace(resultMap, true);
//
//        // Class parameterType = parameterMapBuilder.type();
//        Class<?> javaTypeClass = resolveParameterJavaType(parameterType, property, javaType, jdbcType);
//        TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
//
//        return new ParameterMapping.Builder(configuration, property, javaTypeClass)
//                .jdbcType(jdbcType)
//                .resultMapId(resultMap)
//                .mode(parameterMode)
//                .numericScale(numericScale)
//                .typeHandler(typeHandlerInstance)
//                .build();
//    }

    public mybatis.parser.model.ResultMap addResultMap(
            String id,
            String type,
            String extend,
            mybatis.parser.model.Discriminator discriminator,
            List<ResultMapChild> resultMappings,
            Boolean autoMapping) {
        id = applyCurrentNamespace(id, false);
        extend = applyCurrentNamespace(extend, true);

        if (extend != null) {
            if (!configuration.hasResultMap(extend)) {
                throw new IncompleteElementException("Could not find a parent resultmap with id '" + extend + "'");
            }
            mybatis.parser.model.ResultMap resultMap = configuration.getResultMap(extend);
            List<ResultMapChild> extendedResultMappings = new ArrayList<>(resultMap.getResultMappings());
            extendedResultMappings.removeAll(resultMappings);
            // Remove parent constructor if this resultMap declares a constructor.
            boolean declaresConstructor = false;
            for (ResultMapChild resultMapping : resultMappings) {
                if (resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR)) {
                    declaresConstructor = true;
                    break;
                }
            }
            if (declaresConstructor) {
                extendedResultMappings.removeIf(resultMapping -> resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR));
            }
            resultMappings.addAll(extendedResultMappings);
        }
        mybatis.parser.model.ResultMap resultMap = mybatis.parser.model.ResultMap.ResultMapBuilder.aResultMap()
                .withConfiguration(configuration)
                .withId(id)
                .withType(type)
                .withResultMappings(resultMappings)
                .withAutoMapping(autoMapping)
                .withDiscriminator(discriminator)
                .build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

    public mybatis.parser.model.Discriminator buildDiscriminator(
            String resultType,
            String column,
            String javaType,
            JdbcType jdbcType,
            String typeHandlerType,
            Map<String, String> discriminatorMap) {
        ResultMapChild resultMapChild = buildResultMapping(
                resultType,
                null,
                column,
                javaType,
                jdbcType,
                null,
                null,
                null,
                null,
                typeHandlerType,
                new ArrayList<>(),
                null,
                null,
                false);
        Map<String, String> namespaceDiscriminatorMap = new HashMap<>();
        for (Map.Entry<String, String> e : discriminatorMap.entrySet()) {
            String resultMap = e.getValue();
            resultMap = applyCurrentNamespace(resultMap, true);
            namespaceDiscriminatorMap.put(e.getKey(), resultMap);
        }
        return mybatis.parser.model.Discriminator.DiscriminatorBuilder.aDiscriminator()
                .withDiscriminatorMap(namespaceDiscriminatorMap)
                .withResultMapChild(resultMapChild).build();
//                (configuration, resultMapChild, namespaceDiscriminatorMap).build();
    }

    public MapperStatement addMappedStatement(
            String id,
            BoundSqlStatementSource sqlSource,
            StatementType statementType,
            SqlCommandType sqlCommandType,
            Integer fetchSize,
            Integer timeout,
            String parameterMap,
            String parameterType,
//            Class<?> parameterType,
            String resultMap,
            String resultType,
//            Class<?> resultType,
            ResultSetType resultSetType,
            boolean flushCache,
            boolean useCache,
            boolean resultOrdered,
            mybatis.parser.keygen.KeyGenerator keyGenerator,
            String keyProperty,
            String keyColumn,
            String databaseId,
            String lang,
//            LanguageDriver lang,
            String resultSets,
            String body) {

        if (unresolvedCacheRef) {
            throw new IncompleteElementException("Cache-ref not yet resolved");
        }

        id = applyCurrentNamespace(id, false);
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

        MapperStatement.MapperStatementBuilder statementBuilder = MapperStatement.MapperStatementBuilder.aMapperStatement()
                .withConfiguration(configuration)
                .withId(id)
                .withSqlSource(sqlSource)
                .withSqlCommandType(sqlCommandType)
                .withResource(resource)
                .withFetchSize(fetchSize)
                .withTimeout(timeout)
                .withStatementType(statementType)
                .withKeyGenerator(keyGenerator)
                .withKeyProperty(keyProperty)
                .withKeyColumn(keyColumn)
                .withDatabaseId(databaseId)
                .withLang(lang)
                .withResultOrdered(resultOrdered)
                .withResultSets(resultSets)
                .withResultMaps(getStatementResultMaps(resultMap, resultType, id)) //TODO IncompleteElementException occurs when resultmap is not found
                .withResultSetType(resultSetType)
                .withFlushCacheRequired(valueOrDefault(flushCache, !isSelect))
                .withUseCache(valueOrDefault(useCache, isSelect))
                .withCache(null)
                .withParameterType(parameterType)
                .withBody(body);

        mybatis.parser.model.ParameterMap statementParameterMap = getStatementParameterMap(parameterMap, parameterType, id);
        if (statementParameterMap != null) {
            statementBuilder.withParameterMap(statementParameterMap);
        }

        MapperStatement statement = statementBuilder.build();
        configuration.addMappedStatement(statement);
        return statement;
    }

    /**
     * Backward compatibility signature 'addMappedStatement'.
     *
     * @param id
     *          the id
     * @param sqlSource
     *          the sql source
     * @param statementType
     *          the statement type
     * @param sqlCommandType
     *          the sql command type
     * @param fetchSize
     *          the fetch size
     * @param timeout
     *          the timeout
     * @param parameterMap
     *          the parameter map
     * @param parameterType
     *          the parameter type
     * @param resultMap
     *          the result map
     * @param resultType
     *          the result type
     * @param resultSetType
     *          the result set type
     * @param flushCache
     *          the flush cache
     * @param useCache
     *          the use cache
     * @param resultOrdered
     *          the result ordered
     * @param keyGenerator
     *          the key generator
     * @param keyProperty
     *          the key property
     * @param keyColumn
     *          the key column
     * @param databaseId
     *          the database id
     * @param lang
     *          the lang
     * @return the mapped statement
     */
//    public MapperStatement addMappedStatement(String id, BoundSqlStatementSource sqlSource, StatementType statementType,
//                                              SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap, String parameterType,
//                                              String resultMap, String resultType, ResultSetType resultSetType, boolean flushCache, boolean useCache,
//                                              boolean resultOrdered, mybatis.parser.keygen.KeyGenerator keyGenerator, String keyProperty, String keyColumn, String databaseId,
//                                              String lang) {
//        return addMappedStatement(
//                id, sqlSource, statementType, sqlCommandType, fetchSize, timeout,
//                parameterMap, parameterType, resultMap, resultType, resultSetType,
//                flushCache, useCache, resultOrdered, keyGenerator, keyProperty,
//                keyColumn, databaseId, lang, null);
//    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    private mybatis.parser.model.ParameterMap getStatementParameterMap(
            String parameterMapName,
            String parameterType,
            String statementId) {
        parameterMapName = applyCurrentNamespace(parameterMapName, true);
        mybatis.parser.model.ParameterMap parameterMap = null;
        if (parameterMapName != null) {
            try {
                parameterMap = configuration.getParameterMap(parameterMapName);
            } catch (IllegalArgumentException e) {
                throw new IncompleteElementException("Could not find parameter map " + parameterMapName, e);
            }
        } else if (parameterType != null) {
            List<ParameterMapChild> parameterMappings = new ArrayList<>();
            parameterMap = mybatis.parser.model.ParameterMap.ParameterMapBuilder
                    .aParameterMap()
                    .withId(statementId + "-Inline")
                    .withType(parameterType)
                    .withParameterMappings(parameterMappings)
                    .build();
        }
        return parameterMap;
    }

    private List<mybatis.parser.model.ResultMap> getStatementResultMaps(
            String resultMap,
            String resultType,
            String statementId) {
        resultMap = applyCurrentNamespace(resultMap, true);

        List<mybatis.parser.model.ResultMap> resultMaps = new ArrayList<>();
        if (resultMap != null) {
            String[] resultMapNames = resultMap.split(",");
            for (String resultMapName : resultMapNames) {
                try {
                    resultMaps.add(configuration.getResultMap(resultMapName.trim()));
                } catch (IllegalArgumentException e) {
//                    throw new IncompleteElementException("Could not find result map '" + resultMapName + "' referenced from '" + statementId + "'", e);
                    logger.warn("Report why an exception occurred", e);
                }
            }
        } else if (resultType != null) {
            mybatis.parser.model.ResultMap inlineResultMap = mybatis.parser.model.ResultMap.ResultMapBuilder.aResultMap()
                    .withId(statementId + "-Inline")
                    .withType(resultType)
                    .withResultMappings(new ArrayList<>())
                    .withAutoMapping(null)
                    .build();
            resultMaps.add(inlineResultMap);
        }
        return resultMaps;
    }

    public ResultMapChild buildResultMapping(
//            Class<?> resultType,
            String resultType,
            String property,
            String column,
//            Class<?> javaType,
            String javaType,
            JdbcType jdbcType,
            String nestedSelect,
            String nestedResultMap,
            String notNullColumn,
            String columnPrefix,
//            Class<? extends TypeHandler<?>> typeHandler,
            String typeHandlerType,
            List<ResultFlag> flags,
            String resultSet,
            String foreignColumn,
            boolean lazy) {
        String resolvedJavaType = resolveResultJavaType(resultType, property, javaType);
//        TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
        List<ResultMapChild> composites;
        if ((nestedSelect == null || nestedSelect.isEmpty()) && (foreignColumn == null || foreignColumn.isEmpty())) {
            composites = Collections.emptyList();
        } else {
            composites = parseCompositeColumnName(column);
        }
        return ResultMapChild.ResultMapChildBuilder.aResultMapChild()
                .withConfiguration(configuration)
                .withProperty(property)
                .withColumn(column)
                .withJavaType(resolvedJavaType)
                .withJdbcType(jdbcType)
                .withNestedQueryId(applyCurrentNamespace(nestedSelect, true))
                .withNestedResultMapId(applyCurrentNamespace(nestedResultMap, true))
                .withResultSet(resultSet)
                .withTypeHandlerType(typeHandlerType)
                .withFlags(flags == null ? new ArrayList<>() : flags)
                .withComposites(composites)
                .withNotNullColumns(parseMultipleColumnNames(notNullColumn))
                .withColumnPrefix(columnPrefix)
                .withForeignColumn(foreignColumn)
                .withLazy(lazy)
                .build();

    }

    private Set<String> parseMultipleColumnNames(String columnName) {
        Set<String> columns = new HashSet<>();
        if (columnName != null) {
            if (columnName.indexOf(',') > -1) {
                StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
                while (parser.hasMoreTokens()) {
                    String column = parser.nextToken();
                    columns.add(column);
                }
            } else {
                columns.add(columnName);
            }
        }
        return columns;
    }

        private List<ResultMapChild> parseCompositeColumnName(String columnName) {
        List<ResultMapChild> composites = new ArrayList<>();
        if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
            StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
            while (parser.hasMoreTokens()) {
                String property = parser.nextToken();
                String column = parser.nextToken();
                ResultMapChild complexResultMapping = ResultMapChild.ResultMapChildBuilder.aResultMapChild()
                        .withProperty(property)
                        .withColumn(column)
                        .build();
                composites.add(complexResultMapping);
            }
        }
        return composites;
    }

//    private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
//        if (javaType == null && property != null) {
//            try {
//                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
//                javaType = metaResultType.getSetterType(property);
//            } catch (Exception e) {
//                // ignore, following null check statement will deal with the situation
//            }
//        }
//        if (javaType == null) {
//            javaType = Object.class;
//        }
//        return javaType;
//    }

//    private Class<?> resolveParameterJavaType(Class<?> resultType, String property, Class<?> javaType, JdbcType jdbcType) {
//        if (javaType == null) {
//            if (JdbcType.CURSOR.equals(jdbcType)) {
//                javaType = java.sql.ResultSet.class;
//            } else if (Map.class.isAssignableFrom(resultType)) {
//                javaType = Object.class;
//            } else {
//                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
//                javaType = metaResultType.getGetterType(property);
//            }
//        }
//        if (javaType == null) {
//            javaType = Object.class;
//        }
//        return javaType;
//    }

    private String resolveResultJavaType(String resultType, String property, String javaType) {
        if (javaType == null && property != null) {
            try {
                var resultTypeClass  = configuration.getTypeResolver().resolveClass(resultType);
                var wrapper = resultTypeClass.map(c -> ClassTypeWrapper.create(c));
                if(wrapper.isPresent()){
                    javaType = wrapper.get().getSetterType(property).declaredName;
                }
            } catch (Exception e) {
                // ignore, following null check statement will deal with the situation
            }
        }
//        if (javaType == null) {
//            javaType = Object.class.getName();
//        }
        return javaType;
    }

}
