package mybatis.diagnostics;

import mybatis.diagnostics.analysis.jdbc.JDBCAnalysisService;
import mybatis.diagnostics.analysis.jdbc.model.NamedJDBCType;
import mybatis.diagnostics.analysis.structure.StructureAnalysisService;
import mybatis.diagnostics.analysis.structure.visitor.DefaultContextProvider;
import mybatis.diagnostics.analysis.structure.visitor.StatementSymbolSet;
import mybatis.diagnostics.analysis.structure.visitor.delete.DeleteSymbolSet;
import mybatis.diagnostics.analysis.structure.visitor.insert.InsertSymbolSet;
import mybatis.diagnostics.analysis.structure.visitor.select.SelectSymbolSet;
import mybatis.diagnostics.analysis.structure.visitor.update.UpdateSymbolSet;
import mybatis.diagnostics.event.GroupEvent;
import mybatis.diagnostics.event.TextEvent;
import mybatis.diagnostics.exception.DatabaseObjectNameCheckException;
import mybatis.diagnostics.exception.TypeCompatibilityCheckException;
import mybatis.diagnostics.model.DiagnosticSource;
import mybatis.diagnostics.model.DiagnosticType;
import mybatis.diagnostics.model.context.Context;
import mybatis.diagnostics.model.error.*;
import mybatis.diagnostics.report.IncomptatibleTypeErrorReport;
import mybatis.diagnostics.report.ObjectNameNotFoundErrorReport;
import mybatis.diagnostics.report.SyntaxErrorReport;
import mybatis.diagnostics.report.UnresolvedJavaTypeErrorReport;
import mybatis.parser.model.MapperStatement;
import mybatis.type.jtj.JavaToJdbcMappingTable;
import mybatis.util.MapperUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.util.validation.metadata.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MapperStatementDiagnostics {
    Logger logger = LoggerFactory.getLogger(MapperStatementDiagnostics.class);
    private JavaToJdbcMappingTable javaJdbcMappingTable = new JavaToJdbcMappingTable();

    public MapperStatementDiagnostics() {
    }

    /**
     * Reference: net/sf/jsqlparser/util/validation/ParseCapability.java
     *
     * @param statement
     * @return
     */
    public SyntaxErrorReport.SyntaxErrorReportBuilder checkGrammar(MapperStatement statement) {
        SyntaxErrorReport.SyntaxErrorReportBuilder builder = SyntaxErrorReport.SyntaxErrorReportBuilder.aSyntaxErrorReport();
        builder.diagnosisStart(LocalDateTime.now());
        var executableSqlWithSignature = statement.getBoundSql(new HashMap<>()).toString();
        var executableSql = MapperUtil.trimSignature(executableSqlWithSignature);
        var context = new DefaultContextProvider() {
        }.createValidationContext();
        Optional<SyntaxError> optionalError = Optional.empty();
        try {
            CCJSqlParserUtil.parseStatements(CCJSqlParserUtil.newParser(executableSql)
                    .withConfiguration(context.getConfiguration()));
        } catch (JSQLParserException e) {
            optionalError = Optional.of(new SyntaxError(statement, e));
        }
        builder.errorList(optionalError.map(err -> List.of(err)).orElse(List.of()));
        builder.diagnosisEnd(LocalDateTime.now());
        return builder;
    }

    public ObjectNameNotFoundErrorReport.ObjectNameNotFoundErrorReportBuilder checkDatabaseObjectName(MapperStatement statement) throws DatabaseObjectNameCheckException {
        var builder = ObjectNameNotFoundErrorReport.ObjectNameNotFoundErrorReportBuilder.anObjectNameNotFoundErrorReport();
        builder.diagnosisStart(LocalDateTime.now());
        List<ObjectNameNotFoundError> errorList = new ArrayList<>();
        var executableSqlWithSignature = statement.getBoundSql(new HashMap<>()).toString();
        var executableSql = MapperUtil.trimSignature(executableSqlWithSignature);
        try (var connection = statement.getConfiguration().getConnection()) {
            var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
            var structureMeta = StructureAnalysisService.getSymbol(StructureAnalysisService.parseStatement(executableSql));
            Map<Named, List<ASTNodeAccess>> map = structureMeta.getColumnNodeMap();
            meta.getColumnExistMap().forEach((key, value) -> {
                if (!value) {
                    map.get(key).forEach(access -> {
                        errorList.add(new ObjectNameNotFoundError(statement, new ObjectNameNotFoundErrorSource(key, access)));
                    });
                }
            });
        } catch (JSQLParserException | SQLException e) {
            throw new DatabaseObjectNameCheckException("While checking object name was valid, the parsing error occurred.", e);
        }
        builder.errorList(errorList);
        builder.diagnosisEnd(LocalDateTime.now());
        return builder;
    }

    public UnresolvedJavaTypeErrorReport.UnresolvedJavaTypeErrorReportBuilder checkTypeExistence(MapperStatement statement) {
        var builder = UnresolvedJavaTypeErrorReport.UnresolvedJavaTypeErrorReportBuilder.anUnresolvedJavaTypeErrorReport();
        builder.diagnosisStart(LocalDateTime.now());
        List<UnresolvedJavaTypeError> errorList = new ArrayList<>();
        statement.getSqlSource().getParameterMappings().forEach(mapping -> {
            if (isUnboundParameterType(statement.getParameterType()) == false
                    && !mapping.getResolvingType().isResolved()) {
                errorList.add(new UnresolvedJavaTypeError(statement, mapping));
            }
        });
        builder.diagnosisEnd(LocalDateTime.now());
        builder.errorList(errorList);
        return builder;
    }

    boolean isUnboundParameterType(String parameterType) {
        return parameterType == null ||
                parameterType.equalsIgnoreCase("map") ||
                parameterType.equalsIgnoreCase("hashmap") ||
                parameterType.equalsIgnoreCase("map") ||
                parameterType.equalsIgnoreCase(Map.class.getName()) ||
                parameterType.equalsIgnoreCase("hashmap") ||
                parameterType.equalsIgnoreCase(HashMap.class.getName()) ||
                parameterType.equalsIgnoreCase("list") ||
                parameterType.equalsIgnoreCase(List.class.getName()) ||
                parameterType.equalsIgnoreCase("arraylist") ||
                parameterType.equalsIgnoreCase(ArrayList.class.getName()) ||
                parameterType.equalsIgnoreCase("collection") ||
                parameterType.equalsIgnoreCase(Collection.class.getName()) ||
                parameterType.equalsIgnoreCase("iterator") ||
                parameterType.equalsIgnoreCase(Iterator.class.getName());
    }

    public IncomptatibleTypeErrorReport.IncomptatibleTypeErrorReportBuilder checkTypeCompatibility(MapperStatement statement, GroupEvent mapperEventGroup) throws TypeCompatibilityCheckException {
        var builder = IncomptatibleTypeErrorReport.IncomptatibleTypeErrorReportBuilder.anIncomptatibleTypeErrorReport();
        builder.diagnosisStart(LocalDateTime.now());
        List<IncompatibleTypeError> errorList = new ArrayList<>();
        var context = new Context<>(new DiagnosticSource(DiagnosticType.TypeCompatibility), statement);
        var eventGroup = mapperEventGroup.group(context);
        var boundSql = statement.getBoundSql(new HashMap<>());
        eventGroup.log(new TextEvent(context, "Got bound SQL Statement"));
        var executableSqlWithSignature = boundSql.toString();
        eventGroup.log(new TextEvent(context, "Got executable SQL statement"));

        var executableSql = MapperUtil.trimSignature(executableSqlWithSignature);
        var parameterIdList = MapperUtil.getParameterIdList(executableSqlWithSignature);
        eventGroup.log(new TextEvent(context, String.format("Unique parameter ids: %s", parameterIdList)));
        var mappings = boundSql.getParameterMappings();
        eventGroup.log(new TextEvent(context, String.format("Parameter notation count: %d", mappings.size())));
        Map<Named, NamedJDBCType> columnTypeMap = null;
        StatementSymbolSet symbolSet = null;
        try (var connection = statement.getConfiguration().getConnection()) {
            eventGroup.log(new TextEvent(context, String.format("Extracting column information...")));
            var meta = JDBCAnalysisService.getMetadata(connection, executableSql);
            columnTypeMap = meta.getColumnTypeMap();
            symbolSet = StructureAnalysisService.getSymbol(StructureAnalysisService.parseStatement(executableSql));
        } catch (SQLException | JSQLParserException e) {
            eventGroup.log(new TextEvent(context, String.format("An exception occurs: %s", e.toString())));
            eventGroup.log(new TextEvent(context, String.format("There is no available map for column types.")));
            throw new TypeCompatibilityCheckException("While checking type compatibility, the parsing error occurred.", e);
        }
        eventGroup.log(new TextEvent(context, String.format("ColumnTypeMap has %d entries", columnTypeMap != null ? columnTypeMap.size() : 0)));
        var typeCompatibilityDiagnostics = new TypeCompatibilityDiagnostics(columnTypeMap, mappings, parameterIdList, statement, javaJdbcMappingTable);
//        try {
//        } catch (JSQLParserException e) {
//            throw new TypeCompatibilityCheckException("While checking type compatibility, the parsing error occurred.", e);
//        }
        if (symbolSet instanceof SelectSymbolSet) {
            eventGroup.log(new TextEvent(context, String.format("Statement type is Select")));
            typeCompatibilityDiagnostics.checkBinaryExpressionList(((SelectSymbolSet) symbolSet).binaryExpressions, eventGroup).forEach(item -> errorList.add(item));
        } else if (symbolSet instanceof InsertSymbolSet) {
            eventGroup.log(new TextEvent(context, String.format("Statement type is Insert")));
            typeCompatibilityDiagnostics.checkPairList(((InsertSymbolSet) symbolSet).pairs, eventGroup).forEach(item -> errorList.add(item));
        } else if (symbolSet instanceof UpdateSymbolSet) {
            eventGroup.log(new TextEvent(context, String.format("Statement type is Update")));
            typeCompatibilityDiagnostics.checkBinaryExpressionList(((UpdateSymbolSet) symbolSet).binaryExpressionsExceptForSet, eventGroup).forEach(item -> errorList.add(item));
            ;
            typeCompatibilityDiagnostics.checkPairList(((UpdateSymbolSet) symbolSet).simpleSetPairs, eventGroup).forEach(item -> errorList.add(item));
        } else if (symbolSet instanceof DeleteSymbolSet) {
            eventGroup.log(new TextEvent(context, String.format("Statement type is Delete")));
            typeCompatibilityDiagnostics.checkBinaryExpressionList(((DeleteSymbolSet) symbolSet).binaryExpressions, eventGroup).forEach(item -> errorList.add(item));
            ;
        } else
            eventGroup.log(new TextEvent(context, String.format("Other Statement type is not supported")));
        eventGroup.log(new TextEvent(context, String.format("All jobs of Type Compatibility have been finished")));
        builder.diagnosisEnd(LocalDateTime.now());
        builder.errorList(errorList);
        return builder;
    }
//
//    private void checkBinaryExpressionList(List<BinaryExpression> binaryExpressions, Map<Named, NamedJDBCType> columnTypeMap, List<ParameterMapChild> params, String signature, MapperStatement statement, GroupEvent eventGroup) {
//        var context = new Context<>(new DiagnosticSource(DiagnosticType.TypeCompatibility), statement);
//        eventGroup.log(new TextEvent(context, String.format("%d Binary expressions have been found", binaryExpressions.size())));
//
//        binaryExpressions.forEach(b -> {
//            if (b instanceof ColumnValueExpression) {
//                var cvContext = new Context<>(new DiagnosticSource(DiagnosticType.ColumnValueExpression), statement);
//                var columnType = columnTypeMap.get(new Named(NamedObject.column, ((ColumnValueExpression) b).getColumn().getFullyQualifiedName()));
//                var cvEvent = eventGroup.group(new TextEvent(cvContext, String.format("Column Value expression is %s", b)), cvContext);
//                cvEvent.log(new TextEvent(cvContext, String.format("For Column, Name: %s, JDBCType : %s", ((ColumnValueExpression) b).getColumn().getFullyQualifiedName(), columnType)));
//                Pattern p = Pattern.compile("^'?" + signature + "(\\d+)'?$");
//                Matcher mat = p.matcher(((ColumnValueExpression) b).getLiteral().toString());
//                if (mat.find()) {
//                    cvEvent.log(new TextEvent(cvContext, String.format("Signature matched %s", signature)));
//                    var paramIndex = Integer.parseInt(mat.group(1));
//                    var param = params.get(paramIndex);
//                    var javaType = params.get(paramIndex).getJavaType();
//                    if(!param.getResolvingType().isResolved()){
//                        cvEvent.log(new TextEvent(cvContext, String.format("No resolved type for %s", param.getNotation().getToken())));
//                        return;
//                    }
//                    cvEvent.log(new TextEvent(cvContext, String.format("For Literal, Value: %s, Order: %d", ((ColumnValueExpression) b).getLiteral(), paramIndex)));
//                    cvEvent.log(new TextEvent(cvContext, String.format("For Literal, Java Type: %s, JDBCType list: %s", javaType
//                            , javaJdbcMappingTable.findJdbcTypeSet(javaType)
//                                    .stream()
//                                    .map(t -> t.getName() + ":" + t.getVendorTypeNumber())
//                                    .collect(Collectors.toSet()))));
//                } else {
//                    cvEvent.log(new TextEvent(cvContext, String.format("No signature matched")));
//                }
//            } else {
//                eventGroup.log(new TextEvent(context, String.format("Not Column Value expression is %s", b)));
//                return;
//            }
//        });
//    }
//
//    private void checkPairList(List<Pair<Column, Expression>> pairs, Map<Named, NamedJDBCType> columnTypeMap, List<ParameterMapChild> children, String signature, MapperStatement statement, GroupEvent eventGroup) {
//        var context = new Context<>(new DiagnosticSource(DiagnosticType.TypeCompatibility), statement);
//        eventGroup.log(new TextEvent(context, String.format("%d Pairs have been found", pairs.size())));
//
//        pairs.forEach(b -> {
//            var pairContext = new Context<>(new DiagnosticSource(DiagnosticType.PairExpression), statement);
//            var columnType = columnTypeMap.get(new Named(NamedObject.column, (b.getKey().getFullyQualifiedName())));
//            var pairEvent = eventGroup.group(new TextEvent(pairContext, String.format("Pair expression is %s", b)), pairContext);
//            pairEvent.log(new TextEvent(pairContext, String.format("For Column, Name: %s, JDBCType : %s", b.getKey().getFullyQualifiedName(), columnType)));
//            Pattern p = Pattern.compile("^'?" + signature + "(\\d+)'?$");
//            Matcher mat = p.matcher(b.getValue().toString());
//            if (mat.find()) {
//                pairEvent.log(new TextEvent(pairContext, String.format("Signature matched %s", signature)));
//                var index = Integer.parseInt(mat.group(1));
//                var javaType = children.get(index).getJavaType();
//                pairEvent.log(new TextEvent(pairContext, String.format("For Literal, Value: %s, Order: %d", b.getValue(), index)));
//                if (javaJdbcMappingTable.findJdbcTypeSet(javaType) != null) {
//                    pairEvent.log(new TextEvent(pairContext, String.format("For Literal, Java Type: %s, JDBCType list: %s", javaType
//                            , javaJdbcMappingTable.findJdbcTypeSet(javaType)
//                                    .stream()
//                                    .map(t -> t.getName() + ":" + t.getVendorTypeNumber())
//                                    .collect(Collectors.toSet()))));
//                } else {
//                    pairEvent.log(new TextEvent(pairContext, String.format("For Literal, Java Type: %s, JDBCType list is empty[]", javaType)));
//                }
//            } else {
//                pairEvent.log(new TextEvent(pairContext, String.format("No signature matched")));
//            }
//        });
//    }

    void checkStatementAttributes(MapperStatement statement) {
        throw new UnsupportedOperationException();
    }

}
