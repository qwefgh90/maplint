package mybatis.parser.keygen;

import mybatis.executor.Executor;
import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;

import java.sql.Statement;
import java.util.List;

public class SelectKeyGenerator implements KeyGenerator{


    public static final String SELECT_KEY_SUFFIX = "!selectKey";
    private final boolean executeBefore;
    private final MapperStatement keyStatement;

    public SelectKeyGenerator(MapperStatement keyStatement, boolean executeBefore) {
        this.executeBefore = executeBefore;
        this.keyStatement = keyStatement;
    }

    @Override
    public void processBefore(mybatis.executor.Executor executor, MapperStatement ms, Statement stmt, Object parameter) {
        if (executeBefore) {
            processGeneratedKeys(executor, ms, parameter);
        }
    }

    @Override
    public void processAfter(Executor executor, MapperStatement ms, Statement stmt, Object parameter) {
        if (!executeBefore) {
            processGeneratedKeys(executor, ms, parameter);
        }
    }

    //TODO
    private void processGeneratedKeys(Executor executor, MapperStatement ms, Object parameter) {
//        try {
//            if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
//                String[] keyProperties = keyStatement.getKeyProperties();
//                final Config configuration = ms.getConfiguration();
//                final MetaObject metaParam = configuration.newMetaObject(parameter);
//                // Do not close keyExecutor.
//                // The transaction will be closed by parent executor.
//                Executor keyExecutor = configuration.newExecutor(executor.getTransaction(), ExecutorType.SIMPLE);
//                List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
//                if (values.size() == 0) {
//                    throw new ExecutorException("SelectKey returned no data.");
//                } else if (values.size() > 1) {
//                    throw new ExecutorException("SelectKey returned more than one value.");
//                } else {
//                    MetaObject metaResult = configuration.newMetaObject(values.get(0));
//                    if (keyProperties.length == 1) {
//                        if (metaResult.hasGetter(keyProperties[0])) {
//                            setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
//                        } else {
//                            // no getter for the property - maybe just a single value object
//                            // so try that
//                            setValue(metaParam, keyProperties[0], values.get(0));
//                        }
//                    } else {
//                        handleMultipleProperties(keyProperties, metaParam, metaResult);
//                    }
//                }
//            }
//        } catch (ExecutorException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
//        }
    }

    private void handleMultipleProperties(String[] keyProperties,
                                          MetaObject metaParam, MetaObject metaResult) {
        String[] keyColumns = keyStatement.getKeyColumns();

        if (keyColumns == null || keyColumns.length == 0) {
            // no key columns specified, just use the property names
            for (String keyProperty : keyProperties) {
                setValue(metaParam, keyProperty, metaResult.getValue(keyProperty));
            }
        } else {
            if (keyColumns.length != keyProperties.length) {
                throw new ExecutorException("If SelectKey has key columns, the number must match the number of key properties.");
            }
            for (int i = 0; i < keyProperties.length; i++) {
                setValue(metaParam, keyProperties[i], metaResult.getValue(keyColumns[i]));
            }
        }
    }

    private void setValue(MetaObject metaParam, String property, Object value) {
        if (metaParam.hasSetter(property)) {
            metaParam.setValue(property, value);
        } else {
            throw new ExecutorException("No setter found for the keyProperty '" + property + "' in " + metaParam.getOriginalObject().getClass().getName() + ".");
        }
    }

}
