package mybatis.diagnostics.report;

import mybatis.diagnostics.model.error.UnresolvedJavaTypeError;
import report.BaseErrorReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qwefgh90
 */
public class UnresolvedJavaTypeErrorReport extends BaseErrorReport<UnresolvedJavaTypeError> {
    public UnresolvedJavaTypeErrorReport(List<UnresolvedJavaTypeError> errorList, LocalDateTime diagnosisStart, LocalDateTime diagnosisEnd) {
        super(errorList, diagnosisStart, diagnosisEnd);
    }

    public static final class UnresolvedJavaTypeErrorReportBuilder {
        List<UnresolvedJavaTypeError> errorList;
        LocalDateTime diagnosisStart;
        LocalDateTime diagnosisEnd;

        private UnresolvedJavaTypeErrorReportBuilder() {
        }

        public static UnresolvedJavaTypeErrorReportBuilder anUnresolvedJavaTypeErrorReport() {
            return new UnresolvedJavaTypeErrorReportBuilder();
        }

        public UnresolvedJavaTypeErrorReportBuilder errorList(List<UnresolvedJavaTypeError> errorList) {
            this.errorList = errorList;
            return this;
        }

        public UnresolvedJavaTypeErrorReportBuilder diagnosisStart(LocalDateTime diagnosisStart) {
            this.diagnosisStart = diagnosisStart;
            return this;
        }

        public UnresolvedJavaTypeErrorReportBuilder diagnosisEnd(LocalDateTime diagnosisEnd) {
            this.diagnosisEnd = diagnosisEnd;
            return this;
        }

        public UnresolvedJavaTypeErrorReport build() {
            assert diagnosisStart != null;
            assert diagnosisEnd != null;
            assert errorList != null;
            UnresolvedJavaTypeErrorReport unresolvedJavaTypeErrorReport = new UnresolvedJavaTypeErrorReport(errorList, diagnosisStart, diagnosisEnd);
            return unresolvedJavaTypeErrorReport;
        }
    }
}
