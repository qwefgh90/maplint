package mybatis.diagnostics.report;

import mybatis.diagnostics.model.error.IncompatibleTypeError;
import report.BaseErrorReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qwefgh90
 */
public class IncomptatibleTypeErrorReport extends BaseErrorReport<IncompatibleTypeError> {
    public IncomptatibleTypeErrorReport(List<IncompatibleTypeError> errorList, LocalDateTime diagnosisStart, LocalDateTime diagnosisEnd) {
        super(errorList, diagnosisStart, diagnosisEnd);
    }

    public static final class IncomptatibleTypeErrorReportBuilder {
        List<IncompatibleTypeError> errorList;
        LocalDateTime diagnosisStart;
        LocalDateTime diagnosisEnd;

        private IncomptatibleTypeErrorReportBuilder() {
        }

        public static IncomptatibleTypeErrorReportBuilder anIncomptatibleTypeErrorReport() {
            return new IncomptatibleTypeErrorReportBuilder();
        }

        public IncomptatibleTypeErrorReportBuilder errorList(List<IncompatibleTypeError> errorList) {
            this.errorList = errorList;
            return this;
        }

        public IncomptatibleTypeErrorReportBuilder diagnosisStart(LocalDateTime diagnosisStart) {
            this.diagnosisStart = LocalDateTime.now();
            return this;
        }

        public IncomptatibleTypeErrorReportBuilder diagnosisEnd(LocalDateTime diagnosisEnd) {
            this.diagnosisEnd = LocalDateTime.now();
            return this;
        }

        public IncomptatibleTypeErrorReport build() {
            assert diagnosisStart != null;
            assert diagnosisEnd != null;
            assert errorList != null;
            IncomptatibleTypeErrorReport incomptatibleTypeErrorReport = new IncomptatibleTypeErrorReport(errorList, diagnosisStart, diagnosisEnd);
            return incomptatibleTypeErrorReport;
        }
    }
}
