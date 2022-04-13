package mybatis.parser.model;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import java.util.List;
import java.util.Set;

public class ResultMapChild {

    private String property;
    private String column;
    private String javaType;
    private JdbcType jdbcType;
    private String typeHandlerType;
    private String nestedResultMapId;
    private String nestedQueryId;
    private Set<String> notNullColumns;
    private String columnPrefix;
    private List<ResultFlag> flags;
    private List<ResultMapChild> composites;
    private String resultSet;
    private String foreignColumn;
    private boolean lazy;
    private Config configuration;

    public ResultMapChild(String property, String column, String javaType, JdbcType jdbcType, String typeHandlerType, String nestedResultMapId, String nestedQueryId, Set<String> notNullColumns, String columnPrefix, List<ResultFlag> flags, List<ResultMapChild> composites, String resultSet, String foreignColumn, boolean lazy, Config configuration) {
        this.property = property;
        this.column = column;
        this.javaType = javaType;
        this.jdbcType = jdbcType;
        this.typeHandlerType = typeHandlerType;
        this.nestedResultMapId = nestedResultMapId;
        this.nestedQueryId = nestedQueryId;
        this.notNullColumns = notNullColumns;
        this.columnPrefix = columnPrefix;
        this.flags = flags;
        this.composites = composites;
        this.resultSet = resultSet;
        this.foreignColumn = foreignColumn;
        this.lazy = lazy;
        this.configuration = configuration;
    }

    public static final class ResultMapChildBuilder {
        private String property;
        private String column;
        private String javaType;
        private JdbcType jdbcType;
        private String typeHandlerType;
        private String nestedResultMapId;
        private String nestedQueryId;
        private Set<String> notNullColumns;
        private String columnPrefix;
        private List<ResultFlag> flags;
        private List<ResultMapChild> composites;
        private String resultSet;
        private String foreignColumn;
        private boolean lazy;
        private Config configuration;

        private ResultMapChildBuilder() {
        }

        public static ResultMapChildBuilder aResultMapChild() {
            return new ResultMapChildBuilder();
        }

        public ResultMapChildBuilder withProperty(String property) {
            this.property = property;
            return this;
        }

        public ResultMapChildBuilder withColumn(String column) {
            this.column = column;
            return this;
        }

        public ResultMapChildBuilder withJavaType(String javaType) {
            this.javaType = javaType;
            return this;
        }

        public ResultMapChildBuilder withJdbcType(JdbcType jdbcType) {
            this.jdbcType = jdbcType;
            return this;
        }

        public ResultMapChildBuilder withTypeHandlerType(String typeHandlerType) {
            this.typeHandlerType = typeHandlerType;
            return this;
        }

        public ResultMapChildBuilder withNestedResultMapId(String nestedResultMapId) {
            this.nestedResultMapId = nestedResultMapId;
            return this;
        }

        public ResultMapChildBuilder withNestedQueryId(String nestedQueryId) {
            this.nestedQueryId = nestedQueryId;
            return this;
        }

        public ResultMapChildBuilder withNotNullColumns(Set<String> notNullColumns) {
            this.notNullColumns = notNullColumns;
            return this;
        }

        public ResultMapChildBuilder withColumnPrefix(String columnPrefix) {
            this.columnPrefix = columnPrefix;
            return this;
        }

        public ResultMapChildBuilder withFlags(List<ResultFlag> flags) {
            this.flags = flags;
            return this;
        }

        public ResultMapChildBuilder withComposites(List<ResultMapChild> composites) {
            this.composites = composites;
            return this;
        }

        public ResultMapChildBuilder withResultSet(String resultSet) {
            this.resultSet = resultSet;
            return this;
        }

        public ResultMapChildBuilder withForeignColumn(String foreignColumn) {
            this.foreignColumn = foreignColumn;
            return this;
        }

        public ResultMapChildBuilder withLazy(boolean lazy) {
            this.lazy = lazy;
            return this;
        }

        public ResultMapChildBuilder withConfiguration(Config configuration) {
            this.configuration = configuration;
            return this;
        }

        public ResultMapChild build() {
            return new ResultMapChild(property, column, javaType, jdbcType, typeHandlerType, nestedResultMapId, nestedQueryId, notNullColumns, columnPrefix, flags, composites, resultSet, foreignColumn, lazy, configuration);
        }
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getTypeHandlerType() {
        return typeHandlerType;
    }

    public void setTypeHandlerType(String typeHandlerType) {
        this.typeHandlerType = typeHandlerType;
    }

    public String getNestedResultMapId() {
        return nestedResultMapId;
    }

    public void setNestedResultMapId(String nestedResultMapId) {
        this.nestedResultMapId = nestedResultMapId;
    }

    public String getNestedQueryId() {
        return nestedQueryId;
    }

    public void setNestedQueryId(String nestedQueryId) {
        this.nestedQueryId = nestedQueryId;
    }

    public Set<String> getNotNullColumns() {
        return notNullColumns;
    }

    public void setNotNullColumns(Set<String> notNullColumns) {
        this.notNullColumns = notNullColumns;
    }

    public String getColumnPrefix() {
        return columnPrefix;
    }

    public void setColumnPrefix(String columnPrefix) {
        this.columnPrefix = columnPrefix;
    }

    public List<ResultFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<ResultFlag> flags) {
        this.flags = flags;
    }

    public List<ResultMapChild> getComposites() {
        return composites;
    }

    public void setComposites(List<ResultMapChild> composites) {
        this.composites = composites;
    }

    public String getResultSet() {
        return resultSet;
    }

    public void setResultSet(String resultSet) {
        this.resultSet = resultSet;
    }

    public String getForeignColumn() {
        return foreignColumn;
    }

    public void setForeignColumn(String foreignColumn) {
        this.foreignColumn = foreignColumn;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public Config getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Config configuration) {
        this.configuration = configuration;
    }
}
