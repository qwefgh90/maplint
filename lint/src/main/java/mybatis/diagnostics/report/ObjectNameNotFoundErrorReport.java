package mybatis.diagnostics.report;

import mybatis.diagnostics.model.error.ObjectNameNotFoundError;
import report.BaseErrorReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qwefgh90
 */
public class ObjectNameNotFoundErrorReport extends BaseErrorReport<ObjectNameNotFoundError> {
    public ObjectNameNotFoundErrorReport(List<ObjectNameNotFoundError> errorList, LocalDateTime diagnosisStart, LocalDateTime diagnosisEnd) {
        super(errorList, diagnosisStart, diagnosisEnd);
    }

    public static final class ObjectNameNotFoundErrorReportBuilder {
        List<ObjectNameNotFoundError> errorList;
        LocalDateTime diagnosisStart;
        LocalDateTime diagnosisEnd;

        private ObjectNameNotFoundErrorReportBuilder() {
        }

        public static ObjectNameNotFoundErrorReportBuilder anObjectNameNotFoundErrorReport() {
            return new ObjectNameNotFoundErrorReportBuilder();
        }

        public ObjectNameNotFoundErrorReportBuilder errorList(List<ObjectNameNotFoundError> errorList) {
            this.errorList = errorList;
            return this;
        }

        public ObjectNameNotFoundErrorReportBuilder diagnosisStart(LocalDateTime diagnosisStart) {
            this.diagnosisStart = diagnosisStart;
            return this;
        }

        public ObjectNameNotFoundErrorReportBuilder diagnosisEnd(LocalDateTime diagnosisEnd) {
            this.diagnosisEnd = diagnosisEnd;
            return this;
        }

        public ObjectNameNotFoundErrorReport build() {
            assert diagnosisStart != null;
            assert diagnosisEnd != null;
            assert errorList != null;
            ObjectNameNotFoundErrorReport objectNameNotFoundErrorReport = new ObjectNameNotFoundErrorReport(errorList, diagnosisStart, diagnosisEnd);
            return objectNameNotFoundErrorReport;
        }
    }
}
