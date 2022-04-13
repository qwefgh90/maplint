package mybatis.diagnostics.report;

import mybatis.diagnostics.model.error.base.Error;
import mybatis.parser.model.MapperStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qwefgh90
 */
public class MappedStatementReport {
    DiagnosticPhase phase;
    SyntaxErrorReport syntaxErrorReport;
    UnresolvedJavaTypeErrorReport unresolvedJavaTypeErrorReport;
    ObjectNameNotFoundErrorReport objectNameNotFoundErrorReport;
    IncomptatibleTypeErrorReport incomptatibleTypeErrorReport;
    List<Error> errorList;
    MapperStatement mapperStatement;

    protected MappedStatementReport() {
    }

    public DiagnosticPhase getPhase() {
        return phase;
    }

    public SyntaxErrorReport getSyntaxErrorReport() {
        return syntaxErrorReport;
    }

    public UnresolvedJavaTypeErrorReport getUnresolvedJavaTypeErrorReport() {
        return unresolvedJavaTypeErrorReport;
    }

    public ObjectNameNotFoundErrorReport getObjectNameNotFoundErrorReport() {
        return objectNameNotFoundErrorReport;
    }

    public IncomptatibleTypeErrorReport getIncomptatibleTypeErrorReport() {
        return incomptatibleTypeErrorReport;
    }

    public MapperStatement getMapperStatement() {
        return mapperStatement;
    }

    @Override
    public String toString() {
        return "\n\uD83D\uDCDD " + mapperStatement.getId() +
                "\n ⌛ syntaxErrorReport=" + syntaxErrorReport +
                "\n ⌛ unresolvedJavaTypeErrorReport=" + unresolvedJavaTypeErrorReport +
                "\n ⌛ objectNameNotFoundErrorReport=" + objectNameNotFoundErrorReport +
                "\n ⌛ incomptatibleTypeErrorReport=" + incomptatibleTypeErrorReport;
    }

    public List<Error> getErrorList() {
        return errorList;
    }

    public static final class MappedStatementReportBuilder {
        DiagnosticPhase phase = DiagnosticPhase.ZERO;
        SyntaxErrorReport syntaxErrorReport;
        UnresolvedJavaTypeErrorReport unresolvedJavaTypeErrorReport;
        ObjectNameNotFoundErrorReport objectNameNotFoundErrorReport;
        IncomptatibleTypeErrorReport incomptatibleTypeErrorReport;
        List<Error> errorList = new ArrayList<>();
        MapperStatement mapperStatement;

        private MappedStatementReportBuilder() {
        }

        public static MappedStatementReportBuilder aDiagnosticReport() {
            return new MappedStatementReportBuilder();
        }

        public MappedStatementReportBuilder syntaxErrorReport(SyntaxErrorReport.SyntaxErrorReportBuilder builder) {
            this.syntaxErrorReport = builder.build();
//            errorList.addAll(list);
            this.phase = DiagnosticPhase.ONE;
            return this;
        }

        public MappedStatementReportBuilder unresolvedJavaTypeErrorReport(UnresolvedJavaTypeErrorReport.UnresolvedJavaTypeErrorReportBuilder builder) {
//            assert syntaxErrorReport != null;
            this.unresolvedJavaTypeErrorReport = builder.build();
//            errorList.addAll(list);
            this.phase = DiagnosticPhase.TWO;
            return this;
        }

        public MappedStatementReportBuilder objectNameNotFoundErrorReport(ObjectNameNotFoundErrorReport.ObjectNameNotFoundErrorReportBuilder builder) {
//            assert unresolvedJavaTypeErrorReport != null;
            this.objectNameNotFoundErrorReport = builder.build();
            this.phase = DiagnosticPhase.THREE;
            return this;
        }

        public MappedStatementReportBuilder incomptatibleTypeErrorReport(IncomptatibleTypeErrorReport.IncomptatibleTypeErrorReportBuilder builder) {
//            assert objectNameNotFoundErrorReport != null;
            this.incomptatibleTypeErrorReport = builder.build();
            this.phase = DiagnosticPhase.FOUR;
            return this;
        }

        public MappedStatementReport build(MapperStatement mapperStatement) {
            MappedStatementReport diagnosticReport = new MappedStatementReport();
            diagnosticReport.mapperStatement = mapperStatement;
            diagnosticReport.phase = this.phase;
            diagnosticReport.syntaxErrorReport = this.syntaxErrorReport;
            diagnosticReport.incomptatibleTypeErrorReport = this.incomptatibleTypeErrorReport;
            diagnosticReport.objectNameNotFoundErrorReport = this.objectNameNotFoundErrorReport;
            diagnosticReport.unresolvedJavaTypeErrorReport = this.unresolvedJavaTypeErrorReport;
            diagnosticReport.errorList = this.errorList;
            return diagnosticReport;
        }
    }
}
