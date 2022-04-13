package lint.cli;

import mybatis.diagnostics.report.DiagnosisReport;
import report.BaseErrorReport;
import report.TimeBetween;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qwefgh90
 */
public class LintReport extends BaseErrorReport {
    final LintPhase phase;
    final DiagnosisReport diagnosisReport;
    final TimeBetween projectInitializationTime;
    final TimeBetween configParsingTime;
    final TimeBetween printingTime;

    public LintReport(LintPhase phase, DiagnosisReport diagnosisReport, TimeBetween projectInitializationTime, TimeBetween parsingTime, TimeBetween printingTime
    , LocalDateTime start, LocalDateTime end) {
        super(List.of(), start, end);
        this.phase = phase;
        this.diagnosisReport = diagnosisReport;
        this.projectInitializationTime = projectInitializationTime;
        this.configParsingTime = parsingTime;
        this.printingTime = printingTime;
    }

    @Override
    public String toString() {
        return "phase=" + phase +
                ", \n⏱ lint=" + super.toString() +
                ", \n⏱ projectInitializationTime=" + projectInitializationTime +
                ", \n⏱ configParsingTime=" + configParsingTime +
                ", \n⏱ diagnosisReport=" + (diagnosisReport != null ? diagnosisReport.getTimeBetween() : null) +
                ", \n⏱ printingTime=" + printingTime +
                ", \n⏱ mapperStatements=\n" + diagnosisReport.getReportList().toString();
    }

    public static final class LintReportBuilder {
        LintPhase phase = LintPhase.NotInitialized;
        DiagnosisReport diagnosisReport;
        TimeBetween.TimeBetweenBuilder projectInitializationTimeBuilder = TimeBetween.TimeBetweenBuilder.aTimeBetween();
        TimeBetween.TimeBetweenBuilder parsingTimeBuilder = TimeBetween.TimeBetweenBuilder.aTimeBetween();
        TimeBetween.TimeBetweenBuilder printingTimeBuilder = TimeBetween.TimeBetweenBuilder.aTimeBetween();
        LocalDateTime start;
        LocalDateTime end;

        public LintReportBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }
        public LintReportBuilder end(LocalDateTime end) {
            this.end = end;
            return this;
        }

        public TimeBetween.TimeBetweenBuilder getProjectInitializationTimeBuilder() {
            return projectInitializationTimeBuilder;
        }

        public TimeBetween.TimeBetweenBuilder getParsingTimeBuilder() {
            return parsingTimeBuilder;
        }

        public TimeBetween.TimeBetweenBuilder getPrintingTimeBuilder() {
            return printingTimeBuilder;
        }

        private LintReportBuilder() {
        }

        public static LintReportBuilder aLintReport() {
            return new LintReportBuilder();
        }

        public LintReportBuilder diagnosisReport(DiagnosisReport diagnosisReport) {
            this.diagnosisReport = diagnosisReport;
            return this;
        }


        public LintReport build() {
            assert start != null;
            assert end != null;
            if(projectInitializationTimeBuilder.isComplete())
                phase = LintPhase.Initialized;
            if(parsingTimeBuilder.isComplete())
                phase = LintPhase.Parsed;
            if(diagnosisReport != null)
                phase = LintPhase.DiagnosisEnded;
            if(printingTimeBuilder.isComplete())
                phase = LintPhase.Printed;

            return new LintReport(phase, diagnosisReport,
                    projectInitializationTimeBuilder.isComplete() ? projectInitializationTimeBuilder.build() : null,
                    parsingTimeBuilder.isComplete() ? parsingTimeBuilder.build() : null,
                    printingTimeBuilder.isComplete() ? printingTimeBuilder.build() : null,
                    start, end);
        }
    }
}
