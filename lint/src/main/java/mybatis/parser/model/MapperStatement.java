package mybatis.parser.model;

import mybatis.parser.keygen.KeyGenerator;
import mybatis.parser.sql.bound.BoundSqlStatementSource;
import mybatis.scripting.xmltags.LanguageDriver;
import mybatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapperStatement {
    private String resource;
    private Config configuration;
    private String id;
    private Integer fetchSize;
    private Integer timeout;
    private StatementType statementType;
    private ResultSetType resultSetType;
    private BoundSqlStatementSource sqlSource;
    private Cache cache;
    private ParameterMap parameterMap;
    private List<ResultMap> resultMaps;
    private boolean flushCacheRequired;
    private boolean useCache;
    private boolean resultOrdered;
    private SqlCommandType sqlCommandType;
    private KeyGenerator keyGenerator;
    private String[] keyProperties;
    private String[] keyColumns;
    private boolean hasNestedResultMaps;
    private String databaseId;
    private Log statementLog;
    private mybatis.scripting.xmltags.LanguageDriver lang;
    private String[] resultSets;
    private String parameterType;
    private String body;

    public String getBody() {
        return body;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResource() {
        return resource;
    }

    public Config getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public ResultSetType getResultSetType() {
        return resultSetType;
    }

    public BoundSqlStatementSource getSqlSource() {
        return sqlSource;
    }

    public Cache getCache() {
        return cache;
    }

    public ParameterMap getParameterMap() {
        return parameterMap;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public boolean isFlushCacheRequired() {
        return flushCacheRequired;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public boolean isResultOrdered() {
        return resultOrdered;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public String[] getKeyProperties() {
        return keyProperties;
    }

    public String[] getKeyColumns() {
        return keyColumns;
    }

    public boolean isHasNestedResultMaps() {
        return hasNestedResultMaps;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public Log getStatementLog() {
        return statementLog;
    }

    public LanguageDriver getLang() {
        return lang;
    }

    public String[] getResultSets() {
        return resultSets;
    }

    public MapperStatement() {
    }

    public static final class MapperStatementBuilder {
        private String resource;
        private Config configuration;
        private String id;
        private Integer fetchSize;
        private Integer timeout;
        private StatementType statementType;
        private ResultSetType resultSetType;
        private BoundSqlStatementSource sqlSource;
        private Cache cache;
        private ParameterMap parameterMap = ParameterMap.ParameterMapBuilder.aParameterMap()
                .withId("defaultParameterMap")
                .withType(null)
                .withParameterMappings(new ArrayList<>())
                .build();
        // . .Builder(configuration, "defaultParameterMap", null, new ArrayList<>()).build();;
        private List<mybatis.parser.model.ResultMap> resultMaps = new ArrayList<>();
        private boolean flushCacheRequired;
        private boolean useCache;
        private boolean resultOrdered;
        private SqlCommandType sqlCommandType;
        private KeyGenerator keyGenerator;
        private String[] keyProperties;
        private String[] keyColumns;
        private boolean hasNestedResultMaps;
        private String databaseId;
        private Log statementLog;
        private String lang;
        private String[] resultSets;
        private String parameterType;
        private String body;

        private MapperStatementBuilder() {
        }

        public static MapperStatementBuilder aMapperStatement() {
            return new MapperStatementBuilder();
        }

        public MapperStatementBuilder withResource(String resource) {
            this.resource = resource;
            return this;
        }

        public MapperStatementBuilder withBody(String body) {
            this.body = body;
            return this;
        }
        public MapperStatementBuilder withParameterType(String parameterType) {
            this.parameterType = parameterType;
            return this;
        }
        public MapperStatementBuilder withConfiguration(Config configuration) {
            this.configuration = configuration;
            return this;
        }

        public MapperStatementBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public MapperStatementBuilder withFetchSize(Integer fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        public MapperStatementBuilder withTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public MapperStatementBuilder withStatementType(StatementType statementType) {
            this.statementType = statementType;
            return this;
        }

        public MapperStatementBuilder withResultSetType(ResultSetType resultSetType) {
            this.resultSetType = resultSetType == null ? ResultSetType.DEFAULT : resultSetType;
            return this;
        }

        public MapperStatementBuilder withSqlSource(BoundSqlStatementSource sqlSource) {
            this.sqlSource = sqlSource;
            return this;
        }

        public MapperStatementBuilder withCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public MapperStatementBuilder withParameterMap(ParameterMap parameterMap) {
            this.parameterMap = parameterMap;
            return this;
        }

        public MapperStatementBuilder withResultMaps(List<mybatis.parser.model.ResultMap> resultMaps) {
            this.resultMaps = resultMaps;
            return this;
        }

        public MapperStatementBuilder withFlushCacheRequired(boolean flushCacheRequired) {
            this.flushCacheRequired = flushCacheRequired;
            return this;
        }

        public MapperStatementBuilder withUseCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }

        public MapperStatementBuilder withResultOrdered(boolean resultOrdered) {
            this.resultOrdered = resultOrdered;
            return this;
        }

        public MapperStatementBuilder withSqlCommandType(SqlCommandType sqlCommandType) {
            this.sqlCommandType = sqlCommandType;
            return this;
        }

        public MapperStatementBuilder withKeyGenerator(KeyGenerator keyGenerator) {
            this.keyGenerator = keyGenerator;
            return this;
        }

        public MapperStatementBuilder withKeyProperties(String[] keyProperties) {
            this.keyProperties = keyProperties;
            return this;
        }

        public MapperStatementBuilder withKeyProperty(String keyProperty) {
            this.keyProperties = delimitedStringToArray(keyProperty);
            return this;
        }


        public MapperStatementBuilder withKeyColumn(String keyColumn) {
            this.keyColumns = delimitedStringToArray(keyColumn);
            return this;
        }


        public MapperStatementBuilder withResultSets(String resultSet) {
            this.resultSets = delimitedStringToArray(resultSet);
            return this;
        }


        public MapperStatementBuilder withKeyColumns(String[] keyColumns) {
            this.keyColumns = keyColumns;
            return this;
        }

        public MapperStatementBuilder withHasNestedResultMaps(boolean hasNestedResultMaps) {
            this.hasNestedResultMaps = hasNestedResultMaps;
            return this;
        }

        public MapperStatementBuilder withDatabaseId(String databaseId) {
            this.databaseId = databaseId;
            return this;
        }

        public MapperStatementBuilder withStatementLog(Log statementLog) {
            this.statementLog = statementLog;
            return this;
        }

        public MapperStatementBuilder withLang(String lang) {
            this.lang = lang;
            return this;
        }

        public MapperStatementBuilder withResultSets(String[] resultSets) {
            this.resultSets = resultSets;
            return this;
        }

        public MapperStatement build() {
            assert this.configuration != null;
            assert this.id != null;
            assert this.sqlSource != null;
            assert this.lang != null;
            MapperStatement mapperStatement = new MapperStatement();
            mapperStatement.timeout = this.timeout;
            mapperStatement.resource = this.resource;
            mapperStatement.id = this.id;
            mapperStatement.keyGenerator = this.keyGenerator;
            mapperStatement.sqlSource = this.sqlSource;
            mapperStatement.resultOrdered = this.resultOrdered;
            mapperStatement.keyProperties = this.keyProperties;
            mapperStatement.flushCacheRequired = this.flushCacheRequired;
            mapperStatement.useCache = this.useCache;
            mapperStatement.databaseId = this.databaseId;
            mapperStatement.parameterMap = this.parameterMap;
            mapperStatement.fetchSize = this.fetchSize;
            mapperStatement.resultMaps = Collections.unmodifiableList(this.resultMaps);
            mapperStatement.statementLog = this.statementLog;
            mapperStatement.configuration = this.configuration;
            mapperStatement.hasNestedResultMaps = this.hasNestedResultMaps;
            mapperStatement.resultSets = this.resultSets;
            mapperStatement.sqlCommandType = this.sqlCommandType;
            mapperStatement.statementType = this.statementType;
            mapperStatement.resultSetType = this.resultSetType;
            mapperStatement.keyColumns = this.keyColumns;
            mapperStatement.cache = this.cache;
            mapperStatement.body = this.body;
            mapperStatement.parameterType = this.parameterType;
            mapperStatement.lang = new XMLLanguageDriver();
            return mapperStatement;
        }
    }

    public BoundSqlStatement getBoundSql(Object parameterObject) {
        BoundSqlStatement boundSql = sqlSource.getBoundSql(parameterObject);
        List<ParameterMapChild> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            boundSql = new BoundSqlStatement(configuration, boundSql.toString(), parameterMap.getParameterMappings(), parameterObject);
        }

        // check for nested result maps in parameter mappings (issue #30)
        for (ParameterMapChild pm : boundSql.getParameterMappings()) {
            String rmId = pm.getResultMapId();
            if (rmId != null) {
                mybatis.parser.model.ResultMap rm = configuration.getResultMap(rmId);
                if (rm != null) {
                    hasNestedResultMaps |= rm.hasNestedResultMaps();
                }
            }
        }

        return boundSql;
    }

    private static String[] delimitedStringToArray(String in) {
        if (in == null || in.trim().length() == 0) {
            return null;
        } else {
            return in.split(",");
        }
    }
}
