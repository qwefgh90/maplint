package mybatis.diagnostics.model;

public class DiagnosticSource {
    protected DiagnosticType type;

    public DiagnosticSource(DiagnosticType type) {
        this.type = type;
    }

    public DiagnosticType getType() {
        return type;
    }
}
