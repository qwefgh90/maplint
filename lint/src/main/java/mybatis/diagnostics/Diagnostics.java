package mybatis.diagnostics;

import mybatis.diagnostics.event.GroupEvent;
import mybatis.diagnostics.event.TextEvent;
import mybatis.diagnostics.exception.DatabaseObjectNameCheckException;
import mybatis.diagnostics.exception.TypeCompatibilityCheckException;
import mybatis.diagnostics.report.DiagnosisReport;
import mybatis.diagnostics.report.MappedStatementReport;
import mybatis.diagnostics.model.DiagnosticSource;
import mybatis.diagnostics.model.DiagnosticType;
import mybatis.diagnostics.model.context.Context;
import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Diagnostics {
    Logger logger = LoggerFactory.getLogger(Diagnostics.class);
    private Context configContext = new Context(new DiagnosticSource(DiagnosticType.MyBatisConfig), null);
    private GroupEvent configEvent = new GroupEvent(configContext);

    public GroupEvent getConfigEvent() {
        return configEvent;
    }

    private MapperStatementDiagnostics mapperStatementDiagnostics = new MapperStatementDiagnostics();

    public Diagnostics() {
    }

    public DiagnosisReport.DiagnosisReportBuilder execute(Config config){
        DiagnosisReport.DiagnosisReportBuilder builder = DiagnosisReport.DiagnosisReportBuilder.aDiagnosisReport(config);
        var reportList = new ArrayList<MappedStatementReport>();
        builder.diagnosisStart(LocalDateTime.now());
        var statements = new ArrayList<>(config.getMappedStatements());
        var map = new HashMap<String, MapperStatement>();
        statements.forEach(s -> map.put(s.getId(), s));
        var uniqueStatements = new ArrayList<>(map.values());
        uniqueStatements.sort(Comparator.comparing(MapperStatement::getId));
        configEvent.log(new TextEvent(configContext, String.format("Analyzing mybatis codes on %s", config.getMyBatisProjectService().getConfigFile().toString())));
        boolean isConnected = false;
        try {
            config.getConnection();
            isConnected = true;
        } catch (SQLException e) { }
        for(var stmt : uniqueStatements){
            MappedStatementReport.MappedStatementReportBuilder reportBuilder = MappedStatementReport.MappedStatementReportBuilder.aDiagnosticReport();
            final Context context = new Context(new DiagnosticSource(DiagnosticType.MapperStatement), stmt);
            var groupEvent = this.configEvent.group(context);
            groupEvent.log(new TextEvent(context, String.format("Diagnosing %s", stmt.getId())));
            var grammarResult = mapperStatementDiagnostics.checkGrammar(stmt);
            reportBuilder.syntaxErrorReport(grammarResult);
            reportBuilder.unresolvedJavaTypeErrorReport(mapperStatementDiagnostics.checkTypeExistence(stmt));
            try {
                if(isDDL(stmt)) {
                    groupEvent.log(new TextEvent(context, "The DDL script is not supported."));
                }else if(!isConnected){
                    groupEvent.log(new TextEvent(context, "Database connection was not established."));
                }else{
                    try {
                        reportBuilder.objectNameNotFoundErrorReport(mapperStatementDiagnostics.checkDatabaseObjectName(stmt));
                        reportBuilder.incomptatibleTypeErrorReport(mapperStatementDiagnostics.checkTypeCompatibility(stmt, groupEvent));
                        groupEvent.log(new TextEvent(context, String.format("Finished diagnosis of %s", stmt.getId())));
                    } catch (DatabaseObjectNameCheckException e) {
                        groupEvent.log(new TextEvent(context, String.format("Diagnosis of %s has been stopped. DatabaseObjectNameCheckException was caught. %s", stmt.getId(), e)));
                    } catch (TypeCompatibilityCheckException e) {
                        groupEvent.log(new TextEvent(context, String.format("Diagnosis of %s has been stopped. TypeCompatibilityCheckException was caught. %s", stmt.getId(), e)));
                    }
                }
            } catch (JSQLParserException e) {
                groupEvent.log(new TextEvent(context, String.format("Parsing exception was caught. %s", e.getMessage())));
            } finally {
                reportList.add(reportBuilder.build(stmt));
            }
        }
        builder.diagnosisEnd(LocalDateTime.now());
        builder.reportList(reportList);
        return builder;
    }

//    public List<Error> execute(Config config, String mappedStatementId){
//        var errors = new ArrayList<Error>();
//        var statements = new ArrayList<>(config.getMappedStatements());
//        var map = new HashMap<String, MapperStatement>();
//        statements.forEach(s -> map.put(s.getId(), s));
//        var uniqueStatements = map.values().stream().collect(Collectors.toList());
//        uniqueStatements.sort(Comparator.comparing(MapperStatement::getId));
//        configEvent.log(new TextEvent(configContext, String.format("Analyzing mybatis codes on %s", config.getMyBatisProjectService().getConfigFile().toString())));
//        var optionalStmt = uniqueStatements.stream().filter(m -> m.getId().equals(mappedStatementId)).findFirst();
//        if(optionalStmt.isPresent()){
//            var stmt = optionalStmt.get();
//            final Context context = new Context(new DiagnosticSource(DiagnosticType.MapperStatement), stmt);
//            var groupEvent = this.configEvent.group(context);
//            groupEvent.log(new TextEvent(context, String.format("Diagnosing %s", stmt.getId())));
//            mapperStatementDiagnostics.checkGrammar(stmt).map(err -> errors.add(err));
//            mapperStatementDiagnostics.checkTypeExistence(stmt).forEach(err -> errors.add(err));
//            try {
//                if(isDDL(stmt)) {
//                    groupEvent.log(new TextEvent(context, "The DDL script is not supported."));
//                }else{
//                    try {
//                        mapperStatementDiagnostics.checkDatabaseObjectName(stmt).forEach(err -> errors.add(err));
//                        mapperStatementDiagnostics.checkTypeCompatibility(stmt, groupEvent).forEach(err -> errors.add(err));
//                        groupEvent.log(new TextEvent(context, String.format("Finished diagnosis of %s", stmt.getId())));
//                    } catch (DatabaseObjectNameCheckException e) {
//                        groupEvent.log(new TextEvent(context, String.format("Diagnosis of %s has been stopped. DatabaseObjectNameCheckException was caught. %s", stmt.getId(), e)));
//                    } catch (TypeCompatibilityCheckException e) {
//                        groupEvent.log(new TextEvent(context, String.format("Diagnosis of %s has been stopped. TypeCompatibilityCheckException was caught. %s", stmt.getId(), e)));
//
//                    }
//                }
//            } catch (JSQLParserException e) {
//                groupEvent.log(new TextEvent(context, String.format("Parsing exception was caught. %s", e.getMessage())));
//            }
//        }else{
//            configEvent.log(new TextEvent(configContext, String.format("There is no statement which matches %s", mappedStatementId)));
//        }
//        return errors;
//    }

    boolean isDDL(MapperStatement stmt) throws JSQLParserException {
        var executableSql = stmt.getBoundSql(new HashMap<>()).toString();
        var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
        var matched = list.stream().anyMatch(match -> {
            if(match instanceof CreateTable)
                return true;
            return false;
        });
        return matched;
    }
}
