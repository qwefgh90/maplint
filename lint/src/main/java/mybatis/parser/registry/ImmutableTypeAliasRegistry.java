package mybatis.parser.registry;

import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.executor.loader.cglib.CglibProxyFactory;
import org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j.Log4jImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;

import java.util.Map;

public class ImmutableTypeAliasRegistry {
    private final TypeAliasRegistry typeRegistry = new TypeAliasRegistry();
    public ImmutableTypeAliasRegistry() {
        super();
        typeRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);

        typeRegistry.registerAlias("JNDI", JndiDataSourceFactory.class);
        typeRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
        typeRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);

        typeRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
        typeRegistry.registerAlias("FIFO", FifoCache.class);
        typeRegistry.registerAlias("LRU", LruCache.class);
        typeRegistry.registerAlias("SOFT", SoftCache.class);
        typeRegistry.registerAlias("WEAK", WeakCache.class);

        typeRegistry.registerAlias("DB_VENDOR", VendorDatabaseIdProvider.class);

        typeRegistry.registerAlias("XML", XMLLanguageDriver.class);
        typeRegistry.registerAlias("RAW", RawLanguageDriver.class);

        typeRegistry.registerAlias("SLF4J", Slf4jImpl.class);
        typeRegistry.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
        typeRegistry.registerAlias("LOG4J", Log4jImpl.class);
        typeRegistry.registerAlias("LOG4J2", Log4j2Impl.class);
        typeRegistry.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
        typeRegistry.registerAlias("STDOUT_LOGGING", StdOutImpl.class);
        typeRegistry.registerAlias("NO_LOGGING", NoLoggingImpl.class);

        typeRegistry.registerAlias("CGLIB", CglibProxyFactory.class);
        typeRegistry.registerAlias("JAVASSIST", JavassistProxyFactory.class);
    }

    public <T> Class<T> resolveAlias(String string) {
        try {
            return typeRegistry.resolveAlias(string);
        }catch(Exception e){
            return null;
        }
    }

    public Map<String, Class<?>> getTypeAliases() {
        return typeRegistry.getTypeAliases();
    }
}
