package lint.cli;

import lint.cli.option.LintOption;
import lint.cli.option.LogLevel;
import mybatis.diagnostics.Diagnostics;
import mybatis.diagnostics.model.error.IncompatibleTypeError;
import mybatis.diagnostics.model.error.ObjectNameNotFoundError;
import mybatis.diagnostics.model.error.SyntaxError;
import mybatis.diagnostics.model.error.UnresolvedJavaTypeError;
import mybatis.diagnostics.report.DiagnosisReport;
import mybatis.diagnostics.report.MappedStatementReport;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import report.TimeBetween;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author qwefgh90
 */
public class LintService {
    public LintReport lint(LintOption lintOption){
        LintReport.LintReportBuilder reportBuilder = LintReport.LintReportBuilder.aLintReport();
        reportBuilder.start(LocalDateTime.now());
        reportBuilder.getProjectInitializationTimeBuilder().start(LocalDateTime.now());
        var service = new MyBatisProjectService();
        try {
            service.initialize(lintOption.getProjectPath(), lintOption.getConfigFileName() != null ? lintOption.getConfigFileName() : "");
            reportBuilder.getProjectInitializationTimeBuilder().end(LocalDateTime.now());
            reportBuilder.getParsingTimeBuilder().start(LocalDateTime.now());
            var config = service.getParsedConfig();
            reportBuilder.getParsingTimeBuilder().end(LocalDateTime.now());
            var diagnostics = new Diagnostics();
            List<MappedStatementReport> reportList;
            DiagnosisReport diagnosisReport = diagnostics.execute(config).build();
            reportBuilder.diagnosisReport(diagnosisReport);
            reportList = diagnosisReport.getReportList();
            reportBuilder.getPrintingTimeBuilder().start(LocalDateTime.now());
            for(var report : reportList) {
                System.out.println(String.format("\uD83C\uDFAF %s", report.getMapperStatement().getId()));
                if (report.getPhase().ordinal() == 0) {
                    System.out.println(String.format("  ⌛ Any phase hasn't run."));
                }
                if (report.getPhase().ordinal() >= 1) {
                    System.out.println(String.format("  ⌛ the first phase (%s) has finished.", SyntaxError.class.getSimpleName()));
                }
                if (report.getPhase().ordinal() >= 2) {
                    System.out.println(String.format("  ⌛ the second phase (%s) has finished.", UnresolvedJavaTypeError.class.getSimpleName()));
                }
                if (report.getPhase().ordinal() >= 3) {
                    System.out.println(String.format("  ⌛ the third phase (%s) has finished.", ObjectNameNotFoundError.class.getSimpleName()));
                }
                if (report.getPhase().ordinal() >= 4) {
                    System.out.println(String.format("  ⌛ the fourth phase (%s) has finished.", IncompatibleTypeError.class.getSimpleName()));
                }
                System.out.println(String.format("  ✅ Diagnosis has finished."));
                if(report.getSyntaxErrorReport() != null){
                    for(var value : report.getSyntaxErrorReport().getErrorList()){
                        System.out.println(String.format("    🔨\uD83C\uDD82 %s", (lintOption.getPrintLevel() == LogLevel.Summary ? value.getSummary() : value.getDetails())));
                    }
                }
                if(report.getUnresolvedJavaTypeErrorReport() != null){
                    for(var value : report.getUnresolvedJavaTypeErrorReport().getErrorList()){
                        System.out.println(String.format("    🔨\uD83C\uDD84 %s", (lintOption.getPrintLevel() == LogLevel.Summary ? value.getSummary() : value.getDetails())));
                    }
                }
                if(report.getObjectNameNotFoundErrorReport() != null){
                    for(var value : report.getObjectNameNotFoundErrorReport().getErrorList()){
                        System.out.println(String.format("    🔨\uD83C\uDD7E %s", (lintOption.getPrintLevel() == LogLevel.Summary ? value.getSummary() : value.getDetails())));
                    }
                }
                if(report.getIncomptatibleTypeErrorReport() != null){
                    for(var value : report.getIncomptatibleTypeErrorReport().getErrorList()){
                        System.out.println(String.format("    🔨\uD83C\uDD78 %s", (lintOption.getPrintLevel() == LogLevel.Summary ? value.getSummary() : value.getDetails())));
                    }
                }
            }
            reportBuilder.getPrintingTimeBuilder().end(LocalDateTime.now());
        } catch (ConfigNotFoundException e) {
            e.printStackTrace();
        } catch (MyBatisProjectInitializationException e) {
            e.printStackTrace();
        }finally {
            reportBuilder.end(LocalDateTime.now());
        }
        return reportBuilder.build();
    }
}
