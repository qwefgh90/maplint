package mybatis.type.jtj.mysql;

import com.mysql.cj.MysqlType;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * com/mysql/cj/AbstractQueryBindings.java
 */

public class DefaultMySQLTypeCopy {
    public final static Map<Class<?>, MysqlType> DEFAULT_MYSQL_TYPES;
    static {
        var types = new HashMap<Class<?>, MysqlType>();
        types.put(String.class, MysqlType.VARCHAR);
        types.put(java.sql.Date.class, MysqlType.DATE);
        types.put(java.sql.Time.class, MysqlType.TIME);
        types.put(java.sql.Timestamp.class, MysqlType.TIMESTAMP);
        types.put(Byte.class, MysqlType.INT);
        types.put(BigDecimal.class, MysqlType.DECIMAL);
        types.put(Short.class, MysqlType.SMALLINT);
        types.put(Integer.class, MysqlType.INT);
        types.put(Long.class, MysqlType.BIGINT);
        types.put(Float.class, MysqlType.FLOAT); // TODO check; was Types.FLOAT but should be Types.REAL to map to SQL FLOAT
        types.put(Double.class, MysqlType.DOUBLE);
        types.put(byte[].class, MysqlType.BINARY);
        types.put(Boolean.class, MysqlType.BOOLEAN);
        types.put(LocalDate.class, MysqlType.DATE);
        types.put(LocalTime.class, MysqlType.TIME);
        types.put(LocalDateTime.class, MysqlType.DATETIME); // default JDBC mapping is TIMESTAMP, see B-4
        types.put(OffsetTime.class, MysqlType.TIME); // default JDBC mapping is TIME_WITH_TIMEZONE, see B-4
        types.put(OffsetDateTime.class, MysqlType.TIMESTAMP); // default JDBC mapping is TIMESTAMP_WITH_TIMEZONE, see B-4
        types.put(ZonedDateTime.class, MysqlType.TIMESTAMP); // no JDBC mapping is defined
        types.put(Duration.class, MysqlType.TIME);
        types.put(java.sql.Blob.class, MysqlType.BLOB);
        types.put(java.sql.Clob.class, MysqlType.TEXT);
        types.put(BigInteger.class, MysqlType.BIGINT);
        types.put(java.util.Date.class, MysqlType.TIMESTAMP);
        types.put(java.util.Calendar.class, MysqlType.TIMESTAMP);
        types.put(InputStream.class, MysqlType.BLOB);
        DEFAULT_MYSQL_TYPES = Collections.unmodifiableMap(types);
    }
}
