package mybatis.parser.model;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundSqlStatement {
    private final String sql;
    private final List<ParameterMapChild> parameterMappings;
    private final Object parameterObject;
    private final Map<String, Object> additionalParameters;
    private final MetaObject metaParameters;

    public BoundSqlStatement(Config configuration, String sql, List<ParameterMapChild> parameterMappings, Object parameterObject) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterObject = parameterObject;
        this.additionalParameters = new HashMap<>();
        this.metaParameters = configuration.newMetaObject(additionalParameters);
    }

    @Override
    public String toString() {
        return sql;
    }

    public List<ParameterMapChild> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public boolean hasAdditionalParameter(String name) {
        String paramName = new PropertyTokenizer(name).getName();
        return additionalParameters.containsKey(paramName);
    }

    public void setAdditionalParameter(String name, Object value) {
        metaParameters.setValue(name, value);
    }

    public Object getAdditionalParameter(String name) {
        return metaParameters.getValue(name);
    }
}
