package mybatis.type.jtj;

import mybatis.type.jtj.mysql.DefaultMySQLTypeCopy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.*;

public class JavaToJdbcMappingTable extends AbstractJavaToJdbcMappingTable {
    //MySQL 8.0.28
    void initializeFromDefaultMySQLType() {
        for (var entry : DefaultMySQLTypeCopy.DEFAULT_MYSQL_TYPES.entrySet()) {
            putIntoJavaToJdbcMap(entry.getKey().getName(), JDBCType.valueOf(entry.getValue().getJdbcType()));
        }
    }

    public Set<JDBCType> findJdbcTypeSet(Class clazz) {
        return javaToJdbcMap.get(clazz.getName());
    }

    public Set<JDBCType> findJdbcTypeSet(String fullClassName) {
        return javaToJdbcMap.get(fullClassName);
    }

    public JavaToJdbcMappingTable() {
        initializeTypeMappingsFromMyBatisHandlers();

        //MySQL mappings
        initializeFromDefaultMySQLType();
    }

    //MyBatis 3.5.9
    void initializeTypeMappingsFromMyBatisHandlers() {
        arrayTypeHandler();
        bigDecimalTypeHandler();
        bigIntegerTypeHandler();
        blobByteObjectArrayTypeHandler();
        blobInputStreamTypeHandler();
        blobTypeHandler();
        booleanTypeHandler();
        byteArrayTypeHandler();
        byteObjectArrayTypeHandler();
        byteTypeHandler();
        characterTypeHandler();
        clobReaderTypeHandler();
        clobTypeHandler();
        dateOnlyTypeHandler();
        dateTypeHandler();
        doubleTypeHandler();
        enumOrdinalTypeHandler();
        enumTypeHandler();
        floatTypeHandler();
        instantTypeHandler();
        integerTypeHandler();
        japaneseDateTypeHandler();
        localDateTimeTypeHandler();
        localDateTypeHandler();
        localTimeTypeHandler();
        longTypeHandler();
        monthTypeHandler();
        nClobTypeHandler();
        nStringTypeHandler();
        objectTypeHandler();
        offsetDateTimeTypeHandler();
        offsetTimeTypeHandler();
        shortTypeHandler();
        sqlDateTypeHandler();
        sqlTimestampTypeHandler();
        sqlTimeTypeHandler();
        sqlxmlTypeHandler();
        stringTypeHandler();
        timeOnlyTypeHandler();
        yearMonthTypeHandler();
        yearTypeHandler();
        zonedDateTimeTypeHandler();
    }

    private void arrayTypeHandler() {
        //ArrayTypeHandler.java setArray
        putSetArray(java.sql.Array.class.getName());
        putSetArray("Array"); //ClassType.java fullname
    }

    private void bigDecimalTypeHandler() {
        //BigDecimalTypeHandler.java setBigDecimal
        putSetBigDecimal(BigDecimal.class.getName());
    }

    private void bigIntegerTypeHandler() {
        //BigIntegerTypeHandler.java setBigDecimal
        putSetBigDecimal(BigInteger.class.getName());
    }

    private void blobByteObjectArrayTypeHandler() {
        //BlobByteObjectArrayTypeHandler.java setBinaryStream
//        putSetBinaryStream(Byte[].class.getName());
        //TODO All array types become "Array" without ComponentType
        putSetBinaryStream("Array");
        putSetBinaryStream(ByteArrayInputStream.class.getName());

        //C:/Users/qwefgh90/.m2/repository/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28-sources.jar!/com/mysql/cj/ClientPreparedQueryBindings.java:170
//        putSetBlob(Byte[].class.getName());
        //TODO All array types become "Array" without ComponentType
        putSetBlob("Array");
        putSetBlob(ByteArrayInputStream.class.getName());
    }

    private void blobInputStreamTypeHandler() {
        //BlobInputStreamTypeHandler.java setBlob
        putSetBlob(InputStream.class.getName());
    }

    private void blobTypeHandler() {
        //BlobTypeHandler.java setBinaryStream
//        putSetBinaryStream(byte[].class.getName());
        //TODO All array types become "Array" without ComponentType
        putSetBinaryStream("Array");
    }

    private void booleanTypeHandler() {
        //BooleanTypeHandler.java setBoolean
        putSetBoolean(Boolean.class.getName());
        putSetBoolean(boolean.class.getName());
    }

    private void byteArrayTypeHandler() {
        //ByteArrayTypeHandler.java setBytes
//        putSetBytes(byte[].class.getName());
        //TODO All array types become "Array" without ComponentType
        putSetBytes("Array");
    }

    private void byteObjectArrayTypeHandler() {
        //ByteObjectArrayTypeHandler.java setBytes
//        putSetBytes(Byte[].class.getName());
        //TODO All array types become "Array" without ComponentType
        putSetBytes("Array");
    }

    private void byteTypeHandler() {
        //ByteTypeHandler.java setByte
        putSetByte(Byte.class.getName());
        putSetByte(byte.class.getName());
    }

    private void characterTypeHandler() {
        //CharacterTypeHandler.java setString
        putSetString(Character.class.getName());
        putSetString(char.class.getName());
    }


    private void clobReaderTypeHandler() {
        //ClobReaderTypeHandler.java
        putSetClob(Reader.class.getName());
    }

    private void clobTypeHandler() {
        //ClobTypeHandler.java
        putSetCharacterStream(String.class.getName());
    }

    private void dateOnlyTypeHandler() {
        //DateOnlyTypeHandler.java
        putSetDate(Date.class.getName());
    }

    private void dateTypeHandler() {
        //DateTypeHandler.java
        putSetTimestamp(Date.class.getName());
    }

    private void doubleTypeHandler() {
        //DoubleTypeHandler.java
        putSetDouble(Double.class.getName());
        putSetDouble(double.class.getName());
    }

    private void enumOrdinalTypeHandler() {
        //EnumOrdinalTypeHandler.java
        putSetInt(Enum.class.getName());
    }

    private void enumTypeHandler() {
        //EnumTypeHandler.java
        putSetString(Enum.class.getName());
        //TODO ps.setObject(i, parameter.name(), jdbcType.TYPE_CODE);
    }

    private void floatTypeHandler() {
        //FloatTypeHandler.java
        putSetFloat(Float.class.getName());
        putSetFloat(float.class.getName());
    }

    private void instantTypeHandler() {
        //InstantTypeHandler.java
        putSetTimestamp(Instant.class.getName());
    }

    private void integerTypeHandler() {
        //IntegerTypeHandler.java
        putSetInt(Integer.class.getName());
        putSetInt(int.class.getName());
    }

    private void japaneseDateTypeHandler() {
        //JapaneseDateTypeHandler.java
        putSetDate(JapaneseDate.class.getName());
    }

    private void localDateTimeTypeHandler() {
        //LocalDateTimeTypeHandler.java
        putSetObject(LocalDateTime.class.getName());
    }

    private void localDateTypeHandler() {
        //LocalDateTypeHandler.java
        putSetObject(LocalDate.class.getName());
    }

    private void localTimeTypeHandler() {
        //LocalTimeTypeHandler.java
        putSetObject(LocalTime.class.getName());
    }

    private void longTypeHandler() {
        //LongTypeHandler.java
        putSetLong(Long.class.getName());
        putSetLong(long.class.getName());
    }

    private void monthTypeHandler() {
        //MonthTypeHandler.java
        putSetInt(Month.class.getName());
    }

    private void nClobTypeHandler() {
        //NClobTypeHandler.java
        putSetCharacterStream(String.class.getName());
    }

    private void nStringTypeHandler() {
        //NStringTypeHandler.java
        putSetNString(String.class.getName());
    }

    private void objectTypeHandler() {
        //ObjectTypeHandler.java
        putSetObject(Object.class.getName());
    }

    private void offsetDateTimeTypeHandler() {
        //OffsetDateTimeTypeHandler.java
        putSetObject(OffsetDateTime.class.getName());
    }

    private void offsetTimeTypeHandler() {
        //OffsetTimeTypeHandler.java
        putSetObject(OffsetTime.class.getName());
    }

    private void shortTypeHandler() {
        //ShortTypeHandler.java
        putSetShort(Short.class.getName());
        putSetShort(short.class.getName());
    }

    private void sqlDateTypeHandler() {
        //SqlDateTypeHandler.java
        putSetDate(java.sql.Date.class.getName());
    }

    private void sqlTimestampTypeHandler() {
        //SqlTimestampTypeHandler.java
        putSetTimestamp(java.sql.Timestamp.class.getName());
    }

    private void sqlTimeTypeHandler() {
        //SqlTimeTypeHandler.java
        putSetTime(java.sql.Time.class.getName());
    }

    private void sqlxmlTypeHandler() {
        //SqlxmlTypeHandler.java
        putSetSQLXML(String.class.getName());
    }

    private void stringTypeHandler() {
        //StringTypeHandler.java
        putSetString(String.class.getName());
    }

    private void timeOnlyTypeHandler() {
        //TimeOnlyTypeHandler.java
        putSetTime(Date.class.getName());
    }

//    private void unknownTypeHandler(){
//        //UnknownTypeHandler.java
//    }

    private void yearMonthTypeHandler() {
        //YearMonthTypeHandler.java
        putSetString(YearMonth.class.getName());
    }

    private void yearTypeHandler() {
        //YearTypeHandler.java
        putSetInt(Year.class.getName());
    }

    private void zonedDateTimeTypeHandler() {
        //ZonedDateTimeTypeHandler.java
        putSetObject(ZonedDateTime.class.getName());
    }
}
