package sql.analysis.database.model;

import java.sql.JDBCType;

public class NamedJDBCType {
    private String name;
    private JDBCType jdbcType;

    public NamedJDBCType(String name, int code) {
        this.name = name;
        this.jdbcType = JDBCType.valueOf(code);
    }

    public String getName() {
        return name;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    @Override
    public String toString() {
        return "NamedJDBCType{" +
                "name='" + name + '\'' +
                ", jdbcType=" + jdbcType +
                ", code=" + jdbcType.getVendorTypeNumber() +
                '}';
    }
}
