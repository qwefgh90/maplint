package mybatis.type.jtj;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AbstractJavaToJdbcMappingTable {
    protected Map<String, Set<JDBCType>> javaToJdbcMap = new HashMap<>();

    protected void putIntoJavaToJdbcMap(String className, JDBCType type){
        if(javaToJdbcMap.containsKey(className)){
            javaToJdbcMap.get(className).add(type);
        }else{
            var newList = new HashSet<JDBCType>();
            newList.add(type);
            javaToJdbcMap.put(className, newList);
        }
    }

    protected void putSetBinaryStream(String className) {
//        putIntoJavaToJdbcMap(className, JDBCType.BLOB);
        putIntoJavaToJdbcMap(className, JDBCType.LONGVARBINARY);
    }

    protected void putSetBoolean(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.BIT);
        putIntoJavaToJdbcMap(className, JDBCType.BOOLEAN);
    }

    protected void putSetByte(String className) {
//
        putIntoJavaToJdbcMap(className, JDBCType.TINYINT);
    }

    protected void putSetShort(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.SMALLINT);
    }

    protected void putSetInt(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.INTEGER);
    }

    protected void putSetLong(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.BIGINT);
    }

    protected void putSetFloat(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.FLOAT);
        putIntoJavaToJdbcMap(className, JDBCType.REAL);
    }

    protected void putSetDouble(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.DOUBLE);
    }

    protected void putSetBigDecimal(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.NUMERIC);
    }

    protected void putSetString(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.VARCHAR);
        putIntoJavaToJdbcMap(className, JDBCType.LONGVARCHAR);
    }

    protected void putSetBytes(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.VARBINARY);
        putIntoJavaToJdbcMap(className, JDBCType.LONGVARBINARY);
    }

    protected void putSetUnicodeStream(String className) {

    }

    protected void putClearParameters(String className) {

    }

    protected void putAddBatch(String className) {

    }

    protected void putSetRef(String className) {

    }

    protected void putSetArray(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.ARRAY);
    }

    protected void putSetDate(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.DATE);
    }

    protected void putSetTime(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.TIME);
    }

    protected void putSetTimestamp(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.TIMESTAMP);
    }

    protected void putSetNull(String className) {

    }

    protected void putSetURL(String className) {

    }

    protected void putSetRowId(String className) {

    }

    protected void putSetNString(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.NCHAR);
        putIntoJavaToJdbcMap(className, JDBCType.NVARCHAR);
        putIntoJavaToJdbcMap(className, JDBCType.LONGNVARCHAR);
    }

    protected void putSetSQLXML(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.SQLXML);
    }

    protected void putSetObject(String className) {
        // it will be complemented by drivers (MySQLType.java)
    }

    protected void putSetNCharacterStream(String className) {

    }

    protected void putSetCharacterStream(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.LONGNVARCHAR);
    }

    protected void putSetClob(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.CLOB);
    }

    protected void putSetBlob(String className) {
        putIntoJavaToJdbcMap(className, JDBCType.BLOB);
//        putIntoJavaToJdbcMap(className, JDBCType.LONGVARBINARY);
    }

    protected void putSetNClob(String className) {

    }

//    java.sql.JDBCType getJDBC(String fullClassName) {
//
//    }

}
