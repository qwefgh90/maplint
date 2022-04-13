### What we resolve?

We have type information, but it is just a string literal.
We need to resolve things to do next analysis. 

- Creating JDBC Object from Java Type
- Compare JDBC Type with Column Data Type

### The need of JDBC Object to create SQL Statement 

To validate SQL Statement, we need the actual parameter.
Because the parameter is not given before executing the program,
we create the actual parameter by analyzing `typeHandler` and
the class type.

### The need of *Java JDBC Mapping Table* and *Database JDBC Mapping Table*

We can do the check to whether the Java type is compatible to Data type
with these tables.

---

### Java Type Analysis (Parameter Type Analysis)

The parameter can be just the primitive type 
or the complex type. There must be JDBC type
mapped to Java Type. All mapping information
is in `TypeHandler` and `JDBC implementations`. 

Finally, the mapping table will be generated. we call this *J2J Table(Java Type to JDBC Type Mapping Table)*. 
*J2J table* has one-to-many relationship. and *the reverse J2J table* has one-to-many relationship. 

---

##### default MyBatis `TypeHandler` is the good place to start looking for a type pair.

1. TypeHandler has a type parameter.
```java
// 'Date' is what we want to know.
public class DateTypeHandler extends BaseTypeHandler<Date> {
```
2. `set...()` method is called in `setNonNullParameter()`
```java
// A call to 'setTimestamp()' is what we want to know.
@Override
public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
    throws SQLException {
  ps.setTimestamp(i, new Timestamp(parameter.getTime()));
}
```

3. In this example, we get a pair `<Date, Timestamp>`.

4. We repeat this operation with all types of `TypeHandler`
and fill the mapping table with pairs.

- When it metts `setObject()`, it can be skipped/ignored.
- [configuration.html#typeHandlers](https://mybatis.org/mybatis-3/configuration.html#typeHandlers) is also helpful.

---

##### We need the vendor specific information.

We don't know JDBC types in case that it just directly calls `setObject()` in `setNonNullParameter()`.
So we should get the type mapping rule in advance by reviewing 
the JDBC implementation.

1. `DEFAULT_MYSQL_TYPES` in `com/mysql/cj/AbstractQueryBindings.java` has 
MySQL default mapping table.

2. We can get MYSQL types, but it isn't JDBC type.

3. in MySQL Connector/J, A implementation of `SQLType`
contains the mapping rule from `jdbcType` to `MysqlType`. 
We create the reverse mapping rule with `getByJdbcType()` in `com/mysql/cj/MysqlType.java`

4. Finally we update the mapping table.

---

##### Type Check and Created Object

1. `xxx.class.isAssignableFrom(target)` by iterating all supported types.

#### Database JDBC Mapping Table

We can get Data type from the result of parsing SQL and we can create the reverse table 
from the mapping rules from JDBC to MySQL type. 

Finally, the mapping table will be generated. we call this *D2J Table(Data Type to JDBC Type Mapping Table)*.
*D2J table* has one-to-many relationship. and *the reverse D2J table* has one-to-one relationship.

1. Get `MysqlType` object from the full MySQL type name with `getByName()`

2. We create the reverse mapping rule from mysql type to jdbc type
   with `getByJdbcType()` in `com/mysql/cj/MysqlType.java`

3. Finally we fill the *D2J mapping table* with pairs
of the reverse mapping table. 

### Appendix A: Default Type Handler

They are searched in `MyBatis 3.5.9`.

- ArrayTypeHandler.java
- BaseTypeHandler.java
- BigDecimalTypeHandler.java
- BigIntegerTypeHandler.java
- BlobByteObjectArrayTypeHandler.java
- BlobInputStreamTypeHandler.java
- BlobTypeHandler.java
- BooleanTypeHandler.java
- ByteArrayTypeHandler.java
- ByteObjectArrayTypeHandler.java
- ByteTypeHandler.java
- CharacterTypeHandler.java
- ClobReaderTypeHandler.java
- ClobTypeHandler.java
- DateOnlyTypeHandler.java
- DateTypeHandler.java
- DoubleTypeHandler.java
- EnumOrdinalTypeHandler.java
- EnumTypeHandler.java
- FloatTypeHandler.java
- InstantTypeHandler.java
- IntegerTypeHandler.java
- JapaneseDateTypeHandler.java
- LocalDateTimeTypeHandler.java
- LocalDateTypeHandler.java
- LocalTimeTypeHandler.java
- LongTypeHandler.java
- MonthTypeHandler.java
- NClobTypeHandler.java
- NStringTypeHandler.java
- ObjectTypeHandler.java
- OffsetDateTimeTypeHandler.java
- OffsetTimeTypeHandler.java
- ShortTypeHandler.java
- SqlDateTypeHandler.java
- SqlTimestampTypeHandler.java
- SqlTimeTypeHandler.java
- SqlxmlTypeHandler.java
- StringTypeHandler.java
- TimeOnlyTypeHandler.java
- UnknownTypeHandler.java
- YearMonthTypeHandler.java
- YearTypeHandler.java
- ZonedDateTimeTypeHandler.java

### Appendix B: DEFAULT_MYSQL_TYPES

`MysqlType` is the sub class of `java.sql.SQLType`.

```java
DEFAULT_MYSQL_TYPES.put(String.class, MysqlType.VARCHAR);
DEFAULT_MYSQL_TYPES.put(java.sql.Date.class, MysqlType.DATE);
DEFAULT_MYSQL_TYPES.put(java.sql.Time.class, MysqlType.TIME);
DEFAULT_MYSQL_TYPES.put(java.sql.Timestamp.class, MysqlType.TIMESTAMP);
DEFAULT_MYSQL_TYPES.put(Byte.class, MysqlType.INT);
DEFAULT_MYSQL_TYPES.put(BigDecimal.class, MysqlType.DECIMAL);
DEFAULT_MYSQL_TYPES.put(Short.class, MysqlType.SMALLINT);
DEFAULT_MYSQL_TYPES.put(Integer.class, MysqlType.INT);
DEFAULT_MYSQL_TYPES.put(Long.class, MysqlType.BIGINT);
DEFAULT_MYSQL_TYPES.put(Float.class, MysqlType.FLOAT); // TODO check; was Types.FLOAT but should be Types.REAL to map to SQL FLOAT
DEFAULT_MYSQL_TYPES.put(Double.class, MysqlType.DOUBLE);
DEFAULT_MYSQL_TYPES.put(byte[].class, MysqlType.BINARY);
DEFAULT_MYSQL_TYPES.put(Boolean.class, MysqlType.BOOLEAN);
DEFAULT_MYSQL_TYPES.put(LocalDate.class, MysqlType.DATE);
DEFAULT_MYSQL_TYPES.put(LocalTime.class, MysqlType.TIME);
DEFAULT_MYSQL_TYPES.put(LocalDateTime.class, MysqlType.DATETIME); // default JDBC mapping is TIMESTAMP, see B-4
DEFAULT_MYSQL_TYPES.put(OffsetTime.class, MysqlType.TIME); // default JDBC mapping is TIME_WITH_TIMEZONE, see B-4
DEFAULT_MYSQL_TYPES.put(OffsetDateTime.class, MysqlType.TIMESTAMP); // default JDBC mapping is TIMESTAMP_WITH_TIMEZONE, see B-4
DEFAULT_MYSQL_TYPES.put(ZonedDateTime.class, MysqlType.TIMESTAMP); // no JDBC mapping is defined
DEFAULT_MYSQL_TYPES.put(Duration.class, MysqlType.TIME);
DEFAULT_MYSQL_TYPES.put(java.sql.Blob.class, MysqlType.BLOB);
DEFAULT_MYSQL_TYPES.put(java.sql.Clob.class, MysqlType.TEXT);
DEFAULT_MYSQL_TYPES.put(BigInteger.class, MysqlType.BIGINT);
DEFAULT_MYSQL_TYPES.put(java.util.Date.class, MysqlType.TIMESTAMP);
DEFAULT_MYSQL_TYPES.put(java.util.Calendar.class, MysqlType.TIMESTAMP);
DEFAULT_MYSQL_TYPES.put(InputStream.class, MysqlType.BLOB);
```