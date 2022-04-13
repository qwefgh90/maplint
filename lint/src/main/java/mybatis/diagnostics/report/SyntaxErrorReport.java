package mybatis.diagnostics.report;

import mybatis.diagnostics.model.error.SyntaxError;
import report.BaseErrorReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qwefgh90
 */
public class SyntaxErrorReport extends BaseErrorReport<SyntaxError> {
    public SyntaxErrorReport(List<SyntaxError> errorList, LocalDateTime diagnosisStart, LocalDateTime diagnosisEnd) {
        super(errorList, diagnosisStart, diagnosisEnd);
    }



    public static final class SyntaxErrorReportBuilder {
        List<SyntaxError> errorList;
        LocalDateTime diagnosisStart;
        LocalDateTime diagnosisEnd;

        private SyntaxErrorReportBuilder() {
        }

        public static SyntaxErrorReportBuilder aSyntaxErrorReport() {
            return new SyntaxErrorReportBuilder();
        }

        public SyntaxErrorReportBuilder errorList(List<SyntaxError> errorList) {
            this.errorList = errorList;
            return this;
        }

        public SyntaxErrorReportBuilder diagnosisStart(LocalDateTime diagnosisStart) {
            this.diagnosisStart = diagnosisStart;
            return this;
        }

        public SyntaxErrorReportBuilder diagnosisEnd(LocalDateTime diagnosisEnd) {
            this.diagnosisEnd = diagnosisEnd;
            return this;
        }

        public SyntaxErrorReport build() {
            assert diagnosisStart != null;
            assert diagnosisEnd != null;
            assert errorList != null;
            SyntaxErrorReport syntaxErrorReport = new SyntaxErrorReport(errorList, diagnosisStart, diagnosisEnd);
            return syntaxErrorReport;
        }
    }
}
