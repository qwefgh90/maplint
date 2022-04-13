package mybatis.diagnostics;

import mybatis.diagnostics.analysis.jdbc.model.NamedJDBCType;
import mybatis.diagnostics.analysis.structure.expression.ColumnValueExpression;
import mybatis.diagnostics.event.GroupEvent;
import mybatis.diagnostics.event.TextEvent;
import mybatis.diagnostics.model.DiagnosticSource;
import mybatis.diagnostics.model.DiagnosticType;
import mybatis.diagnostics.model.context.Context;
import mybatis.diagnostics.model.error.IncompatibleTypeError;
import mybatis.diagnostics.model.error.IncompatibleTypeErrorSource;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.model.ParameterMapChild;
import mybatis.type.jtj.JavaToJdbcMappingTable;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.validation.metadata.Named;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.JDBCType;
import java.util.*;
import java.util.stream.Collectors;

public class TypeCompatibilityDiagnostics {
    private final Map<Named, NamedJDBCType> columnTypeMap;
    private final List<ParameterMapChild> params;
    private final List<String> parameterIdList;
    private final MapperStatement statement;
    private final JavaToJdbcMappingTable javaJdbcMappingTable;

    public TypeCompatibilityDiagnostics(Map<Named, NamedJDBCType> columnTypeMap, List<ParameterMapChild> params, List<String> parameterIdList, MapperStatement statement, JavaToJdbcMappingTable javaJdbcMappingTable) {
        this.columnTypeMap = columnTypeMap;
        this.params = params;
        this.parameterIdList = parameterIdList;
        this.statement = statement;
        this.javaJdbcMappingTable = javaJdbcMappingTable;
    }

    List<IncompatibleTypeError> checkBinaryExpressionList(List<BinaryExpression> binaryExpressions, GroupEvent eventGroup) {
        List<IncompatibleTypeError> errorList = new ArrayList<>();
        var context = new Context<>(new DiagnosticSource(DiagnosticType.TypeCompatibility), statement);
        eventGroup.log(new TextEvent(context, String.format("%d Binary expressions have been found", binaryExpressions.size())));
        binaryExpressions.forEach(b -> {
            if (b instanceof ColumnValueExpression) {
                var cvContext = new Context<>(new DiagnosticSource(DiagnosticType.ColumnValueExpression), statement);
                var column = ((ColumnValueExpression) b).getColumn();
                var value = ((ColumnValueExpression) b).getLiteral();
                var named = new Named(NamedObject.column, column.getFullyQualifiedName());
                var columnType = columnTypeMap.get(named);
                var cvEvent = eventGroup.group(new TextEvent(cvContext, String.format("Column Value expression is %s", b)), cvContext);
//                Pattern p = Pattern.compile("^'?" + parameterIdList + "(\\d+)'?$");
                var matched = parameterIdList.stream().filter(id -> value.toString().contains(id)).findFirst();//p.matcher(((ColumnValueExpression) b).getLiteral().toString());
                if (columnType == null) {
                    cvEvent.log(new TextEvent(cvContext, String.format("No columnType for %s", named)));
                } else {
                    cvEvent.log(new TextEvent(cvContext, String.format("For Column, Name: %s, JDBCType : %s:%d", ((ColumnValueExpression) b).getColumn().getFullyQualifiedName(), columnType.getJdbcType().getName(), columnType.getJdbcType().getVendorTypeNumber())));
                    if (matched.isPresent()) {
                        var matchedUniqueId = matched.get();
                        cvEvent.log(new TextEvent(cvContext, String.format("unique id matched %s", matchedUniqueId)));
                        var paramIndex = parameterIdList.indexOf(matchedUniqueId);
                        var param = params.get(paramIndex);
                        if (!param.getResolvingType().isResolved()) {
                            cvEvent.log(new TextEvent(cvContext, String.format("No resolved type for %s", param.getNotation().getToken())));
                        }
                        var javaType = params.get(paramIndex).getJavaType();
                        var jdbcTypeSet = javaJdbcMappingTable.findJdbcTypeSet(javaType);
                        if (!check(jdbcTypeSet, columnType)) {
                            var type = new IncompatibleTypeError(statement,
                                    new IncompatibleTypeErrorSource(columnType.getJdbcType(), jdbcTypeSet, named, column, value, Optional.of(b), param));
                            errorList.add(type);
                            cvEvent.log(new TextEvent(cvContext, String.format("A %s type of %s doesn't belong to a JDBC set of %s.", columnType.getName(), named.getFqn(), javaType)));
                        } else {
                            cvEvent.log(new TextEvent(cvContext, String.format("A %s type of %s belongs to a JDBC set of %s.", columnType.getName(), named.getFqn(), javaType)));
                        }
                        cvEvent.log(new TextEvent(cvContext, String.format("For Literal, Value: %s, Notation: %s, Order: %d", value, param.getNotation().getToken(), paramIndex)));
                        cvEvent.log(new TextEvent(cvContext, String.format("For Literal, Java Type: %s, JDBCType list: %s", javaType,
                                jdbcTypeSet != null ? jdbcTypeSet.stream()
                                        .map(t -> t.getName() + ":" + t.getVendorTypeNumber())
                                        .collect(Collectors.toSet())
                                        : null
                        )));
                    } else {
                        cvEvent.log(new TextEvent(cvContext, String.format("No parameterIdList matched")));
                    }
                }
            } else {
                eventGroup.log(new TextEvent(context, String.format("Not Column Value expression is %s", b)));
            }
        });
        return errorList;
    }

    boolean check(Set<JDBCType> jdbcTypeSet, NamedJDBCType namedJDBCType) {
        if (jdbcTypeSet == null || namedJDBCType == null)
            return false;
        return jdbcTypeSet.contains(namedJDBCType.getJdbcType());
    }

    List<IncompatibleTypeError> checkPairList(List<Pair<Column, Expression>> pairs, GroupEvent eventGroup) {
        List<IncompatibleTypeError> errorList = new ArrayList<>();
        var context = new Context<>(new DiagnosticSource(DiagnosticType.TypeCompatibility), statement);
        eventGroup.log(new TextEvent(context, String.format("%d Pairs have been found", pairs.size())));
        pairs.forEach(b -> {
            var cvContext = new Context<>(new DiagnosticSource(DiagnosticType.PairExpression), statement);
            var column = b.getKey();
            var value = b.getValue();
            var named = new Named(NamedObject.column, column.getFullyQualifiedName());
            var columnType = columnTypeMap.get(named);
            var cvEvent = eventGroup.group(new TextEvent(cvContext, String.format("Column Value expression is %s", b)), cvContext);
//            Pattern p = Pattern.compile("^'?" + parameterIdList + "(\\d+)'?$");
//            Matcher mat = p.matcher(value.toString());
            var matched = parameterIdList.stream().filter(id -> value.toString().contains(id)).findFirst();//p.matcher(((ColumnValueExpression) b).getLiteral().toString());
            if (columnType == null) {
                cvEvent.log(new TextEvent(cvContext, String.format("No columnType for %s", named)));
            } else {
                cvEvent.log(new TextEvent(cvContext, String.format("For Column, Name: %s, JDBCType : %s:%d", column.getFullyQualifiedName(), columnType.getJdbcType().getName(), columnType.getJdbcType().getVendorTypeNumber())));
                if (matched.isPresent()) {
                    var matchedUniqueId = matched.get();
                    cvEvent.log(new TextEvent(cvContext, String.format("unique id matched %s", matchedUniqueId)));
                    var paramIndex = parameterIdList.indexOf(matchedUniqueId);
                    var param = params.get(paramIndex);
                    if (!param.getResolvingType().isResolved()) {
                        cvEvent.log(new TextEvent(cvContext, String.format("No resolved type for %s", param.getNotation().getToken())));
                    }
                    var javaType = params.get(paramIndex).getJavaType();
                    var jdbcTypeSet = javaJdbcMappingTable.findJdbcTypeSet(javaType);
                    if (!check(jdbcTypeSet, columnType)) {
                        var type = new IncompatibleTypeError(statement,
                                new IncompatibleTypeErrorSource(columnType.getJdbcType(), jdbcTypeSet, named, column, value, Optional.empty(), param));
                        errorList.add(type);
                        cvEvent.log(new TextEvent(cvContext, String.format("A %s type of %s doesn't belong to a JDBC set of %s.", columnType.getName(), named.getFqn(), javaType)));
                    } else {
                        cvEvent.log(new TextEvent(cvContext, String.format("A %s type of %s belongs to a JDBC set of %s.", columnType.getName(), named.getFqn(), javaType)));
                    }
                    cvEvent.log(new TextEvent(cvContext, String.format("For Literal, Value: %s, Notation: %s, Order: %d", value, param.getNotation().getToken(), paramIndex)));
                    cvEvent.log(new TextEvent(cvContext, String.format("For Literal, Java Type: %s, JDBCType list: %s", javaType,
                            jdbcTypeSet != null ? jdbcTypeSet.stream()
                                    .map(t -> t.getName() + ":" + t.getVendorTypeNumber())
                                    .collect(Collectors.toSet())
                                    : null
                    )));
                } else {
                    cvEvent.log(new TextEvent(cvContext, String.format("No parameterIdList matched")));
                }
            }
        });
        return errorList;
    }
}
