package mybatis.diagnostics.report;

import mybatis.diagnostics.model.error.base.Error;
import mybatis.parser.model.Config;
import report.BaseErrorReport;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qwefgh90
 */
public class DiagnosisReport extends BaseErrorReport {
    final Config config;
    final List<MappedStatementReport> reportList;
    public DiagnosisReport(List errorList, LocalDateTime diagnosisStart, LocalDateTime diagnosisEnd, Config config, List<MappedStatementReport> reportList) {
        super(errorList, diagnosisStart, diagnosisEnd);
        this.config = config;
        this.reportList = reportList;
    }

    public Config getConfig() {
        return config;
    }

    public List<MappedStatementReport> getReportList() {
        return reportList;
    }

    public static final class DiagnosisReportBuilder {
        final Config config;
        List<MappedStatementReport> reportList;
        List<Error> errorList;
        LocalDateTime diagnosisStart;
        LocalDateTime diagnosisEnd;
        Duration elapsedTime;

        private DiagnosisReportBuilder(Config config) {
            this.config = config;
            errorList = new ArrayList<>();
        }

        public static DiagnosisReportBuilder aDiagnosisReport(Config config) {
            return new DiagnosisReportBuilder(config);
        }

        public DiagnosisReportBuilder reportList(List<MappedStatementReport> reportList) {
            this.reportList = reportList;
            return this;
        }

        public DiagnosisReportBuilder errorList(List<Error> errorList) {
            this.errorList = errorList;
            return this;
        }

        public DiagnosisReportBuilder diagnosisStart(LocalDateTime diagnosisStart) {
            this.diagnosisStart = diagnosisStart;
            return this;
        }

        public DiagnosisReportBuilder diagnosisEnd(LocalDateTime diagnosisEnd) {
            this.diagnosisEnd = diagnosisEnd;
            return this;
        }

        public DiagnosisReport build() {
            DiagnosisReport diagnosisReport = new DiagnosisReport(errorList, diagnosisStart, diagnosisEnd, config, reportList);
            return diagnosisReport;
        }
    }
}
