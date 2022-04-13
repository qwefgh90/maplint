package mybatis.diagnostics.model;

public enum DiagnosticType {
    MyBatisConfig("MC"),
    MapperStatement("MS"),
    TypeCompatibility("TC"),
    PairExpression("PE"),
    ColumnValueExpression("CVE");

    String abbreviation;
    DiagnosticType(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
