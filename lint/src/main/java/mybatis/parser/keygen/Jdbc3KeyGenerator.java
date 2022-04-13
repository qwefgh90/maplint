package mybatis.parser.keygen;

import mybatis.executor.Executor;
import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.registry.ImmutableTypeHandlerRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.reflection.ArrayUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.util.MapUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Jdbc3KeyGenerator implements KeyGenerator {

    private static final String SECOND_GENERIC_PARAM_NAME = ParamNameResolver.GENERIC_NAME_PREFIX + "2";

    /**
     * A shared instance.
     *
     * @since 3.4.3
     */
    public static final Jdbc3KeyGenerator INSTANCE = new Jdbc3KeyGenerator();

    private static final String MSG_TOO_MANY_KEYS = "Too many keys are generated. There are only %d target objects. "
            + "You either specified a wrong 'keyProperty' or encountered a driver bug like #1523.";

    @Override
    public void processBefore(Executor executor, MapperStatement ms, Statement stmt, Object parameter) {
        // do nothing
    }

    @Override
    public void processAfter(Executor executor, MapperStatement ms, Statement stmt, Object parameter) {
        processBatch(ms, stmt, parameter);
    }

    public void processBatch(MapperStatement ms, Statement stmt, Object parameter) {
        final String[] keyProperties = ms.getKeyProperties();
        if (keyProperties == null || keyProperties.length == 0) {
            return;
        }
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            final ResultSetMetaData rsmd = rs.getMetaData();
            final Config configuration = ms.getConfiguration();
            if (rsmd.getColumnCount() < keyProperties.length) {
                // Error?
            } else {
                assignKeys(configuration, rs, rsmd, keyProperties, parameter);
            }
        } catch (Exception e) {
            throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void assignKeys(Config configuration, ResultSet rs, ResultSetMetaData rsmd, String[] keyProperties,
                            Object parameter) throws SQLException {
        if (parameter instanceof MapperMethod.ParamMap || parameter instanceof DefaultSqlSession.StrictMap) {
            // Multi-param or single param with @Param
            assignKeysToParamMap(configuration, rs, rsmd, keyProperties, (Map<String, ?>) parameter);
        } else if (parameter instanceof ArrayList && !((ArrayList<?>) parameter).isEmpty()
                && ((ArrayList<?>) parameter).get(0) instanceof MapperMethod.ParamMap) {
            // Multi-param or single param with @Param in batch operation
            assignKeysToParamMapList(configuration, rs, rsmd, keyProperties, (ArrayList<MapperMethod.ParamMap<?>>) parameter);
        } else {
            // Single param without @Param
            assignKeysToParam(configuration, rs, rsmd, keyProperties, parameter);
        }
    }

    private void assignKeysToParam(Config configuration, ResultSet rs, ResultSetMetaData rsmd,
                                   String[] keyProperties, Object parameter) throws SQLException {
        Collection<?> params = collectionize(parameter);
        if (params.isEmpty()) {
            return;
        }
        List<KeyAssigner> assignerList = new ArrayList<>();
        for (int i = 0; i < keyProperties.length; i++) {
            assignerList.add(new KeyAssigner(configuration, rsmd, i + 1, null, keyProperties[i]));
        }
        Iterator<?> iterator = params.iterator();
        while (rs.next()) {
            if (!iterator.hasNext()) {
                throw new ExecutorException(String.format(MSG_TOO_MANY_KEYS, params.size()));
            }
            Object param = iterator.next();
            assignerList.forEach(x -> x.assign(rs, param));
        }
    }

    private void assignKeysToParamMapList(Config configuration, ResultSet rs, ResultSetMetaData rsmd,
                                          String[] keyProperties, ArrayList<MapperMethod.ParamMap<?>> paramMapList) throws SQLException {
        Iterator<MapperMethod.ParamMap<?>> iterator = paramMapList.iterator();
        List<KeyAssigner> assignerList = new ArrayList<>();
        long counter = 0;
        while (rs.next()) {
            if (!iterator.hasNext()) {
                throw new ExecutorException(String.format(MSG_TOO_MANY_KEYS, counter));
            }
            MapperMethod.ParamMap<?> paramMap = iterator.next();
            if (assignerList.isEmpty()) {
                for (int i = 0; i < keyProperties.length; i++) {
                    assignerList
                            .add(getAssignerForParamMap(configuration, rsmd, i + 1, paramMap, keyProperties[i], keyProperties, false)
                                    .getValue());
                }
            }
            assignerList.forEach(x -> x.assign(rs, paramMap));
            counter++;
        }
    }

    private void assignKeysToParamMap(Config configuration, ResultSet rs, ResultSetMetaData rsmd,
                                      String[] keyProperties, Map<String, ?> paramMap) throws SQLException {
        if (paramMap.isEmpty()) {
            return;
        }
        Map<String, Map.Entry<Iterator<?>, List<KeyAssigner>>> assignerMap = new HashMap<>();
        for (int i = 0; i < keyProperties.length; i++) {
            Map.Entry<String, KeyAssigner> entry = getAssignerForParamMap(configuration, rsmd, i + 1, paramMap, keyProperties[i],
                    keyProperties, true);
            Map.Entry<Iterator<?>, List<KeyAssigner>> iteratorPair = MapUtil.computeIfAbsent(assignerMap, entry.getKey(),
                    k -> MapUtil.entry(collectionize(paramMap.get(k)).iterator(), new ArrayList<>()));
            iteratorPair.getValue().add(entry.getValue());
        }
        long counter = 0;
        while (rs.next()) {
            for (Map.Entry<Iterator<?>, List<KeyAssigner>> pair : assignerMap.values()) {
                if (!pair.getKey().hasNext()) {
                    throw new ExecutorException(String.format(MSG_TOO_MANY_KEYS, counter));
                }
                Object param = pair.getKey().next();
                pair.getValue().forEach(x -> x.assign(rs, param));
            }
            counter++;
        }
    }

    private Map.Entry<String, KeyAssigner> getAssignerForParamMap(Config config, ResultSetMetaData rsmd,
                                                                                                                      int columnPosition, Map<String, ?> paramMap, String keyProperty, String[] keyProperties, boolean omitParamName) {
        Set<String> keySet = paramMap.keySet();
        // A caveat : if the only parameter has {@code @Param("param2")} on it,
        // it must be referenced with param name e.g. 'param2.x'.
        boolean singleParam = !keySet.contains(SECOND_GENERIC_PARAM_NAME);
        int firstDot = keyProperty.indexOf('.');
        if (firstDot == -1) {
            if (singleParam) {
                return getAssignerForSingleParam(config, rsmd, columnPosition, paramMap, keyProperty, omitParamName);
            }
            throw new ExecutorException("Could not determine which parameter to assign generated keys to. "
                    + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
                    + "Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are "
                    + keySet);
        }
        String paramName = keyProperty.substring(0, firstDot);
        if (keySet.contains(paramName)) {
            String argParamName = omitParamName ? null : paramName;
            String argKeyProperty = keyProperty.substring(firstDot + 1);
            return MapUtil.entry(paramName, new KeyAssigner(config, rsmd, columnPosition, argParamName, argKeyProperty));
        } else if (singleParam) {
            return getAssignerForSingleParam(config, rsmd, columnPosition, paramMap, keyProperty, omitParamName);
        } else {
            throw new ExecutorException("Could not find parameter '" + paramName + "'. "
                    + "Note that when there are multiple parameters, 'keyProperty' must include the parameter name (e.g. 'param.id'). "
                    + "Specified key properties are " + ArrayUtil.toString(keyProperties) + " and available parameters are "
                    + keySet);
        }
    }

    private Map.Entry<String, KeyAssigner> getAssignerForSingleParam(Config config, ResultSetMetaData rsmd,
                                                                                                                         int columnPosition, Map<String, ?> paramMap, String keyProperty, boolean omitParamName) {
        // Assume 'keyProperty' to be a property of the single param.
        String singleParamName = nameOfSingleParam(paramMap);
        String argParamName = omitParamName ? null : singleParamName;
        return MapUtil.entry(singleParamName, new KeyAssigner(config, rsmd, columnPosition, argParamName, keyProperty));
    }

    private static String nameOfSingleParam(Map<String, ?> paramMap) {
        // There is virtually one parameter, so any key works.
        return paramMap.keySet().iterator().next();
    }

    private static Collection<?> collectionize(Object param) {
        if (param instanceof Collection) {
            return (Collection<?>) param;
        } else if (param instanceof Object[]) {
            return Arrays.asList((Object[]) param);
        } else {
            return Arrays.asList(param);
        }
    }

    private class KeyAssigner {
        private final Config configuration;
        private final ResultSetMetaData rsmd;
        private final ImmutableTypeHandlerRegistry typeHandlerRegistry;
        private final int columnPosition;
        private final String paramName;
        private final String propertyName;
        private TypeHandler<?> typeHandler;

        protected KeyAssigner(Config configuration, ResultSetMetaData rsmd, int columnPosition, String paramName,
                              String propertyName) {
            super();
            this.configuration = configuration;
            this.rsmd = rsmd;
            this.typeHandlerRegistry = ImmutableTypeHandlerRegistry.immutableTypeHandlerRegistry; //TODO
            this.columnPosition = columnPosition;
            this.paramName = paramName;
            this.propertyName = propertyName;
        }

        protected void assign(ResultSet rs, Object param) {
            if (paramName != null) {
                // If paramName is set, param is ParamMap
                param = ((MapperMethod.ParamMap<?>) param).get(paramName);
            }
            MetaObject metaParam = configuration.newMetaObject(param);
            try {
                if (typeHandler == null) {
                    if (metaParam.hasSetter(propertyName)) {
                        Class<?> propertyType = metaParam.getSetterType(propertyName);
                        typeHandler = typeHandlerRegistry.getTypeHandler(propertyType,
                                JdbcType.forCode(rsmd.getColumnType(columnPosition)));
                    } else {
                        throw new ExecutorException("No setter found for the keyProperty '" + propertyName + "' in '"
                                + metaParam.getOriginalObject().getClass().getName() + "'.");
                    }
                }
                if (typeHandler == null) {
                    // Error?
                } else {
                    Object value = typeHandler.getResult(rs, columnPosition);
                    metaParam.setValue(propertyName, value);
                }
            } catch (SQLException e) {
                throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e,
                        e);
            }
        }
    }
}
