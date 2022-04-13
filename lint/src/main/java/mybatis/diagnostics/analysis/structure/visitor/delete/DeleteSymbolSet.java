package mybatis.diagnostics.analysis.structure.visitor.delete;

import mybatis.diagnostics.analysis.structure.expression.ExpressionFactory;
import mybatis.diagnostics.analysis.structure.visitor.StatementSymbolSet;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteSymbolSet extends StatementSymbolSet<Delete> {
    public final List<BinaryExpression> binaryExpressions;

    public DeleteSymbolSet(List<BinaryExpression> binaryExpressions, Delete delete, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        super(delete, columnNodeMap);
        this.binaryExpressions = binaryExpressions.stream().map(b -> ExpressionFactory.wrapExpression(b)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "DeleteSymbolSet{" +
                "binaryExpressions=" + binaryExpressions +
                ", \nStatementSymbolSet=\n" + super.toString() +
                '}';
    }
}
