package mybatis.executor;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.MapperStatement;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Executor {
    ResultHandler NO_RESULT_HANDLER = null;

    int update(MapperStatement ms, Object parameter) throws SQLException;

    <E> List<E> query(MapperStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSqlStatement boundSql) throws SQLException;

    <E> List<E> query(MapperStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    <E> Cursor<E> queryCursor(MapperStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

    List<BatchResult> flushStatements() throws SQLException;

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    CacheKey createCacheKey(MapperStatement ms, Object parameterObject, RowBounds rowBounds, BoundSqlStatement boundSql);

    boolean isCached(MapperStatement ms, CacheKey key);

    void clearLocalCache();

    void deferLoad(MapperStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);

}
