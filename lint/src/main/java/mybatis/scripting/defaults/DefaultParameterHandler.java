package mybatis.scripting.defaults;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.model.ParameterMapChild;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * created from DefaultParameterHandler.java
 */
public class DefaultParameterHandler implements ParameterHandler {
//    private final TypeHandlerRegistry typeHandlerRegistry;
    //TODO

    private final MapperStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSqlStatement boundSql;
    private final Config config;

    public DefaultParameterHandler(MapperStatement mappedStatement, Object parameterObject, BoundSqlStatement boundSql) {
        this.mappedStatement = mappedStatement;
        this.config = mappedStatement.getConfiguration();
//        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapChild> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapChild parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value = null;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
//                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
//                        value = parameterObject;
                    } else {
                        //TODO return default values
                        if(parameterMapping.getJavaType().contains("String")){
                            value = "";
                        }
//                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
//                        value = metaObject.getValue(propertyName);
                    }
                    //TODO
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = config.getJdbcTypeForNull();
                    }
                    try {
                        typeHandler.setParameter(ps, i + 1, value, jdbcType);
                    } catch (TypeException | SQLException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }

}
