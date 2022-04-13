package mybatis.parser.model;

import mybatis.executor.Executor;
import mybatis.executor.SimpleExecutor;
import mybatis.executor.statement.RoutingStatementHandler;
import mybatis.parser.ResultMapResolver;
import mybatis.parser.XMLStatementParser;
import mybatis.project.MyBatisProjectService;
import mybatis.type.jtj.TypeResolver;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.annotation.MethodResolver;
//import org.apache.ibatis.executor.*;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
//import org.apache.ibatis.executor.statement.RoutingStatementHandler;
//import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.plugin.InterceptorChain;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;

public class Config {

    protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;
    protected AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;

    protected boolean cacheEnabled = true;
    protected ProxyFactory proxyFactory = new JavassistProxyFactory(); // #224 Using internal Javassist instead of OGNL

    protected boolean lazyLoadingEnabled = false;
    protected boolean aggressiveLazyLoading;
    protected boolean multipleResultSetsEnabled = true;
    protected boolean useColumnLabel = true;
    protected boolean useGeneratedKeys;
    protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;
    protected Integer defaultStatementTimeout;
    protected Integer defaultFetchSize;
    protected ResultSetType defaultResultSetType;
    protected boolean mapUnderscoreToCamelCase;
    protected boolean safeRowBoundsEnabled;
    protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;
    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;
    protected Set<String> lazyLoadTriggerMethods = new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString"));

    protected boolean safeResultHandlerEnabled = true;
    protected boolean callSettersOnNulls;
    protected boolean useActualParamName = true;
    protected boolean returnInstanceForEmptyRow;
    protected String logPrefix;
    protected boolean shrinkWhitespacesInSql;
//    protected Class<?> defaultSqlProviderType;

    protected boolean nullableOnForEach;

    protected final Map<String, MapperStatement> mappedStatements = new Config.StrictMap<MapperStatement>("Mapped Statements collection")
            .conflictMessageProducer((savedValue, targetValue) ->
                    ". please check " + savedValue.getResource() + " and " + targetValue.getResource());

    protected String databaseId;
    protected Environment environment;
    protected final Map<String, mybatis.parser.model.ResultMap> resultMaps = new Config.StrictMap<>("Result Maps collection");
    //    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);
    protected Properties variables = new Properties();
    protected final Set<String> loadedResources = new HashSet<>();
    protected final Map<String, XNode> sqlFragments = new Config.StrictMap<>("XML fragments parsed from previous mappers");
    protected final Collection<XMLStatementParser> incompleteStatements = new LinkedList<>();
    protected final Collection<ResultMapResolver> incompleteResultMaps = new LinkedList<>();

    protected final Map<String, mybatis.parser.model.ParameterMap> parameterMaps = new Config.StrictMap<>("Parameter Maps collection");
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
    protected final Map<String, mybatis.parser.keygen.KeyGenerator> keyGenerators = new Config.StrictMap<>("Key Generators collection");

    protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<>();
    protected final Collection<MethodResolver> incompleteMethods = new LinkedList<>();
    protected TypeResolver typeResolver;
    protected MyBatisProjectService myBatisProjectService;

    public Config() {
        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
        languageRegistry.register(RawLanguageDriver.class);
    }

    public Connection getConnection() throws SQLException {
        var env = this.getEnvironment();
        var manager = env.getTransactionManager();
        var transaction = manager
                .getTransactionFactory()
                .newTransaction(env.getDataSourceConfig().getDataSource(), null, false);
        return transaction.getConnection();
    }

    public MyBatisProjectService getMyBatisProjectService() {
        return myBatisProjectService;
    }

    public void setMyBatisProjectService(MyBatisProjectService myBatisProjectService) {
        this.myBatisProjectService = myBatisProjectService;
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public TypeResolver getTypeResolver() {return typeResolver;}

    public void setTypeResolver(TypeResolver typeResolver) {this.typeResolver = typeResolver;}

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public Set<String> getLoadedResources() {
        return loadedResources;
    }

    public Map<String, XNode> getSqlFragments() {
        return sqlFragments;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

//    public MapperRegistry getMapperRegistry() {
//        return mapperRegistry;
//    }

    public AutoMappingBehavior getAutoMappingBehavior() {
        return autoMappingBehavior;
    }

    public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
        this.autoMappingBehavior = autoMappingBehavior;
    }

    public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
        return autoMappingUnknownColumnBehavior;
    }

    public void setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
        this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public boolean isLazyLoadingEnabled() {
        return lazyLoadingEnabled;
    }

    public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
        this.lazyLoadingEnabled = lazyLoadingEnabled;
    }

    public boolean isAggressiveLazyLoading() {
        return aggressiveLazyLoading;
    }

    public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
        this.aggressiveLazyLoading = aggressiveLazyLoading;
    }

    public boolean isMultipleResultSetsEnabled() {
        return multipleResultSetsEnabled;
    }

    public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
        this.multipleResultSetsEnabled = multipleResultSetsEnabled;
    }

    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }

    public void setUseColumnLabel(boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public void setDefaultFetchSize(Integer defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public ResultSetType getDefaultResultSetType() {
        return defaultResultSetType;
    }

    public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
        this.defaultResultSetType = defaultResultSetType;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isSafeRowBoundsEnabled() {
        return safeRowBoundsEnabled;
    }

    public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
        this.safeRowBoundsEnabled = safeRowBoundsEnabled;
    }

    public LocalCacheScope getLocalCacheScope() {
        return localCacheScope;
    }

    public void setLocalCacheScope(LocalCacheScope localCacheScope) {
        this.localCacheScope = localCacheScope;
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
        this.jdbcTypeForNull = jdbcTypeForNull;
    }

    public Set<String> getLazyLoadTriggerMethods() {
        return lazyLoadTriggerMethods;
    }

    public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
        this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
    }

    public boolean isSafeResultHandlerEnabled() {
        return safeResultHandlerEnabled;
    }

    public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
        this.safeResultHandlerEnabled = safeResultHandlerEnabled;
    }

    public boolean isCallSettersOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public boolean isUseActualParamName() {
        return useActualParamName;
    }

    public void setUseActualParamName(boolean useActualParamName) {
        this.useActualParamName = useActualParamName;
    }

    public boolean isReturnInstanceForEmptyRow() {
        return returnInstanceForEmptyRow;
    }

    public void setReturnInstanceForEmptyRow(boolean returnInstanceForEmptyRow) {
        this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
    }

    public String getLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public boolean isShrinkWhitespacesInSql() {
        return shrinkWhitespacesInSql;
    }

    public void setShrinkWhitespacesInSql(boolean shrinkWhitespacesInSql) {
        this.shrinkWhitespacesInSql = shrinkWhitespacesInSql;
    }

    public boolean isNullableOnForEach() {
        return nullableOnForEach;
    }

    public void setNullableOnForEach(boolean nullableOnForEach) {
        this.nullableOnForEach = nullableOnForEach;
    }

    public Properties getVariables() {
        return variables;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }


    public Collection<XMLStatementParser> getIncompleteStatements() {
        return incompleteStatements;
    }

    public void addIncompleteStatement(XMLStatementParser incompleteStatement) {
        incompleteStatements.add(incompleteStatement);
    }

    public Collection<ResultMapResolver> getIncompleteResultMaps() {
        return incompleteResultMaps;
    }

    public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
        incompleteResultMaps.add(resultMapResolver);
    }

    private void parsePendingResultMaps() {
        if (incompleteResultMaps.isEmpty()) {
            return;
        }
        synchronized (incompleteResultMaps) {
            boolean resolved;
            IncompleteElementException ex = null;
            do {
                resolved = false;
                Iterator<ResultMapResolver> iterator = incompleteResultMaps.iterator();
                while (iterator.hasNext()) {
                    try {
                        iterator.next().resolve();
                        iterator.remove();
                        resolved = true;
                    } catch (IncompleteElementException e) {
                        ex = e;
                    }
                }
            } while (resolved);
            if (!incompleteResultMaps.isEmpty() && ex != null) {
                // At least one result map is unresolvable.
                throw ex;
            }
        }
    }

    public void addKeyGenerator(String id, mybatis.parser.keygen.KeyGenerator keyGenerator) {
        keyGenerators.put(id, keyGenerator);
    }

    public Collection<String> getKeyGeneratorNames() {
        return keyGenerators.keySet();
    }

    public Collection<mybatis.parser.keygen.KeyGenerator> getKeyGenerators() {
        return keyGenerators.values();
    }

    public mybatis.parser.keygen.KeyGenerator getKeyGenerator(String id) {
        return keyGenerators.get(id);
    }

    public boolean hasKeyGenerator(String id) {
        return keyGenerators.containsKey(id);
    }

    public void addParameterMap(mybatis.parser.model.ParameterMap pm) {
        parameterMaps.put(pm.getId(), pm);
    }

    public Collection<String> getParameterMapNames() {
        return parameterMaps.keySet();
    }

    public Collection<mybatis.parser.model.ParameterMap> getParameterMaps() {
        return parameterMaps.values();
    }

    public mybatis.parser.model.ParameterMap getParameterMap(String id) {
        return parameterMaps.get(id);
    }

    public boolean hasParameterMap(String id) {
        return parameterMaps.containsKey(id);
    }

    protected static class StrictMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -4950446264854982944L;
        private final String name;
        private BiFunction<V, V, String> conflictMessageProducer;

        public StrictMap(String name, int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
            this.name = name;
        }

        public StrictMap(String name, int initialCapacity) {
            super(initialCapacity);
            this.name = name;
        }

        public StrictMap(String name) {
            super();
            this.name = name;
        }

        public StrictMap(String name, Map<String, ? extends V> m) {
            super(m);
            this.name = name;
        }

        /**
         * Assign a function for producing a conflict error message when contains value with the same key.
         * <p>
         * function arguments are 1st is saved value and 2nd is target value.
         *
         * @param conflictMessageProducer A function for producing a conflict error message
         * @return a conflict error message
         * @since 3.5.0
         */
        public Config.StrictMap<V> conflictMessageProducer(BiFunction<V, V, String> conflictMessageProducer) {
            this.conflictMessageProducer = conflictMessageProducer;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V put(String key, V value) {
            if (containsKey(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key
                        + (conflictMessageProducer == null ? "" : conflictMessageProducer.apply(super.get(key), value)));
            }
            if (key.contains(".")) {
                final String shortKey = getShortName(key);
                if (super.get(shortKey) == null) {
                    super.put(shortKey, value);
                }
                //TODO Allow to put ambiguity values
//                else {
//                    super.put(shortKey, (V) new Config.StrictMap.Ambiguity(shortKey));
//                }
            }
            return super.put(key, value);
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (value instanceof Config.StrictMap.Ambiguity) {
                throw new IllegalArgumentException(((Config.StrictMap.Ambiguity) value).getSubject() + " is ambiguous in " + name
                        + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }

        protected static class Ambiguity {
            private final String subject;

            public Ambiguity(String subject) {
                this.subject = subject;
            }

            public String getSubject() {
                return subject;
            }
        }

        private String getShortName(String key) {
            final String[] keyParts = key.split("\\.");
            return keyParts[keyParts.length - 1];
        }
    }

    public void addResultMap(mybatis.parser.model.ResultMap rm) {
        resultMaps.put(rm.getId(), rm);
        checkLocallyForDiscriminatedNestedResultMaps(rm);
        checkGloballyForDiscriminatedNestedResultMaps(rm);
    }

    public void addMappedStatement(MapperStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public Collection<String> getMappedStatementNames() {
        buildAllStatements();
        return mappedStatements.keySet();
    }

    public Collection<MapperStatement> getMappedStatements() {
        buildAllStatements();
        return mappedStatements.values();
    }

    public MapperStatement getMappedStatement(String id) {
        return this.getMappedStatement(id, true);
    }

    public MapperStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }
        return mappedStatements.get(id);
    }

    //TODO
    public Executor newExecutor(Transaction transaction) {
        return newExecutor(transaction, defaultExecutorType);
    }

    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
//        executorType = executorType == null ? defaultExecutorType : executorType;
//        executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
        Executor executor;
//        if (ExecutorType.BATCH == executorType) {
//            executor = new BatchExecutor(this, transaction);
//        } else if (ExecutorType.REUSE == executorType) {
//            executor = new ReuseExecutor(this, transaction);
//        } else {
            executor = new SimpleExecutor(this, transaction);
//        }
//        if (cacheEnabled) {
//            executor = new CachingExecutor(executor);
//        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    protected final InterceptorChain interceptorChain = new InterceptorChain();

    public mybatis.executor.statement.StatementHandler newStatementHandler(Executor executor, MapperStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSqlStatement boundSql) {
        mybatis.executor.statement.StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        statementHandler = (mybatis.executor.statement.StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }

    public ParameterHandler newParameterHandler(MapperStatement mappedStatement, Object parameterObject, BoundSqlStatement boundSql) {
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
    }

    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageRegistry.getDefaultDriver();
    }


//    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler,
//                                                ResultHandler resultHandler, BoundSql boundSql) {
//        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
//        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
//        return resultSetHandler;
//    }

    public Collection<String> getResultMapNames() {
        return resultMaps.keySet();
    }

    public Collection<mybatis.parser.model.ResultMap> getResultMaps() {
        return resultMaps.values();
    }

    public mybatis.parser.model.ResultMap getResultMap(String id) {
        return resultMaps.get(id);
    }

    public boolean hasResultMap(String id) {
        return resultMaps.containsKey(id);
    }

    public boolean hasStatement(String statementName) {
        return hasStatement(statementName, true);
    }

    public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }
        return mappedStatements.containsKey(statementName);
    }
    /*
     * Parses all the unprocessed statement nodes in the cache. It is recommended
     * to call this method once all the mappers are added as it provides fail-fast
     * statement validation.
     */
    protected void buildAllStatements() {
        parsePendingResultMaps();
        if (!incompleteCacheRefs.isEmpty()) {
            synchronized (incompleteCacheRefs) {
                incompleteCacheRefs.removeIf(x -> x.resolveCacheRef() != null);
            }
        }
        if (!incompleteStatements.isEmpty()) {
            synchronized (incompleteStatements) {
                incompleteStatements.removeIf(x -> {
                    x.parseStatementNode();
                    return true;
                });
            }
        }
        if (!incompleteMethods.isEmpty()) {
            synchronized (incompleteMethods) {
                incompleteMethods.removeIf(x -> {
                    x.resolve();
                    return true;
                });
            }
        }
    }

    // Slow but a one time cost. A better solution is welcome.
    protected void checkLocallyForDiscriminatedNestedResultMaps(mybatis.parser.model.ResultMap rm) {
        if (!rm.hasNestedResultMaps() && rm.getDiscriminator() != null) {
            for (Map.Entry<String, String> entry : rm.getDiscriminator().getDiscriminatorMap().entrySet()) {
                String discriminatedResultMapName = entry.getValue();
                if (hasResultMap(discriminatedResultMapName)) {
                    mybatis.parser.model.ResultMap discriminatedResultMap = resultMaps.get(discriminatedResultMapName);
                    if (discriminatedResultMap.hasNestedResultMaps()) {
                        rm.forceNestedResultMaps();
                        break;
                    }
                }
            }
        }
    }

    // Slow but a one time cost. A better solution is welcome.
    protected void checkGloballyForDiscriminatedNestedResultMaps(mybatis.parser.model.ResultMap rm) {
        if (rm.hasNestedResultMaps()) {
            for (Map.Entry<String, mybatis.parser.model.ResultMap> entry : resultMaps.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof mybatis.parser.model.ResultMap) {
                    mybatis.parser.model.ResultMap entryResultMap = (mybatis.parser.model.ResultMap) value;
                    if (!entryResultMap.hasNestedResultMaps() && entryResultMap.getDiscriminator() != null) {
                        Collection<String> discriminatedResultMapNames = entryResultMap.getDiscriminator().getDiscriminatorMap().values();
                        if (discriminatedResultMapNames.contains(rm.getId())) {
                            entryResultMap.forceNestedResultMaps();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "autoMappingBehavior=" + autoMappingBehavior +
                ", autoMappingUnknownColumnBehavior=" + autoMappingUnknownColumnBehavior +
                ", cacheEnabled=" + cacheEnabled +
                ", proxyFactory=" + proxyFactory +
                ", lazyLoadingEnabled=" + lazyLoadingEnabled +
                ", aggressiveLazyLoading=" + aggressiveLazyLoading +
                ", multipleResultSetsEnabled=" + multipleResultSetsEnabled +
                ", useColumnLabel=" + useColumnLabel +
                ", useGeneratedKeys=" + useGeneratedKeys +
                ", defaultExecutorType=" + defaultExecutorType +
                ", defaultStatementTimeout=" + defaultStatementTimeout +
                ", defaultFetchSize=" + defaultFetchSize +
                ", defaultResultSetType=" + defaultResultSetType +
                ", mapUnderscoreToCamelCase=" + mapUnderscoreToCamelCase +
                ", safeRowBoundsEnabled=" + safeRowBoundsEnabled +
                ", localCacheScope=" + localCacheScope +
                ", jdbcTypeForNull=" + jdbcTypeForNull +
                ", lazyLoadTriggerMethods=" + lazyLoadTriggerMethods +
                ", safeResultHandlerEnabled=" + safeResultHandlerEnabled +
                ", callSettersOnNulls=" + callSettersOnNulls +
                ", useActualParamName=" + useActualParamName +
                ", returnInstanceForEmptyRow=" + returnInstanceForEmptyRow +
                ", logPrefix='" + logPrefix + '\'' +
                ", shrinkWhitespacesInSql=" + shrinkWhitespacesInSql +
                ", nullableOnForEach=" + nullableOnForEach +
                ", mappedStatements=" + mappedStatements +
                ", databaseId='" + databaseId + '\'' +
                ", environment=" + environment +
                ", resultMaps=" + resultMaps +
                ", variables=" + variables +
                ", loadedResources=" + loadedResources +
                ", sqlFragments=" + sqlFragments +
                ", incompleteStatements=" + incompleteStatements +
                ", incompleteResultMaps=" + incompleteResultMaps +
                ", parameterMaps=" + parameterMaps +
                ", reflectorFactory=" + reflectorFactory +
                ", objectFactory=" + objectFactory +
                ", objectWrapperFactory=" + objectWrapperFactory +
                ", keyGenerators=" + keyGenerators +
                ", incompleteCacheRefs=" + incompleteCacheRefs +
                ", incompleteMethods=" + incompleteMethods +
                '}';
    }
}
