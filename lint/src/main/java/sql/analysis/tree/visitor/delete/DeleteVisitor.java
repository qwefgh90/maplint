package sql.analysis.tree.visitor.delete;

import sql.analysis.tree.visitor.ASTNodeCollector;
import sql.analysis.tree.visitor.DefaultContextProvider;
import sql.analysis.tree.model.DeleteStructuralData;
import sql.analysis.tree.visitor.select.BinaryExpressionVisitor;
import sql.analysis.base.delete.ModifiedDeleteValidator;
import sql.analysis.base.select.ModifiedExpressionValidator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.delete.Delete;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author qwefgh90
 */
public class DeleteVisitor extends ModifiedDeleteValidator implements DefaultContextProvider, Supplier<DeleteStructuralData> {

    protected Delete delete;
    protected ASTNodeCollector astNodeCollector = new ASTNodeCollector();

    public DeleteVisitor() {
        this.setContext(createValidationContext(astNodeCollector));
        this.expressionCollector = getValidator(BinaryExpressionVisitor.class);
    }

    protected BinaryExpressionVisitor expressionCollector;

    public BinaryExpressionVisitor getExpressionCollector() {
        return expressionCollector;
    }

    @Override
    protected void validateOptionalMultiExpressionList(MultiExpressionList multiExprList) {
        if (multiExprList != null) {
            ModifiedExpressionValidator v = expressionCollector;
            multiExprList.getExpressionLists().stream().map(ExpressionList::getExpressions).flatMap(List::stream)
                    .forEach(e -> e.accept(v));
        }
    }

    @Override
    protected void validateOptionalExpression(Expression expression) {
        validateOptional(expression, e -> e.accept(expressionCollector));
    }

    @Override
    protected void validateOptionalExpressions(List<? extends Expression> expressions) {
        validateOptionalList(expressions, () -> expressionCollector, (o, v) -> o.accept(v));
    }

    @Override
    public void validate(Delete delete) {
        this.delete = delete;
        super.validate(delete);
    }

    @Override
    public DeleteStructuralData get() {
        return new DeleteStructuralData(expressionCollector.getBinaryExpressions(), delete, astNodeCollector.getColumnNodeMap());
    }
}
