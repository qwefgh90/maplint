package sql.analysis.tree.model;

import sql.analysis.tree.expression.ExpressionFactory;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteStructuralData extends SQLStructuralData<Delete> {
    public final List<BinaryExpression> binaryExpressions;

    public DeleteStructuralData(List<BinaryExpression> binaryExpressions, Delete delete, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        super(delete, columnNodeMap);
        this.binaryExpressions = binaryExpressions.stream().map(b -> ExpressionFactory.wrapExpression(b)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "DeleteStructuralData{" +
                "binaryExpressions=" + binaryExpressions +
                ", \nStatementSymbolSet=\n" + super.toString() +
                '}';
    }
}
