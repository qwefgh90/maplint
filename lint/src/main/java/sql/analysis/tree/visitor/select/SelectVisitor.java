package sql.analysis.tree.visitor.select;

import sql.analysis.tree.visitor.ASTNodeCollector;
import sql.analysis.tree.visitor.DefaultContextProvider;
import sql.analysis.base.select.ModifiedExpressionValidator;
import sql.analysis.base.select.ModifiedSelectValidator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import sql.analysis.tree.model.SelectStructuralData;

import java.util.List;
import java.util.function.Supplier;

public class SelectVisitor extends ModifiedSelectValidator implements DefaultContextProvider, Supplier<SelectStructuralData> {
    public SelectVisitor() {
        this.setContext(createValidationContext(astNodeCollector));
        expressionCollector = getValidator(BinaryExpressionVisitor.class);
    }

    protected Select select;
    protected List<SelectItem> selectItems;
    protected BinaryExpressionVisitor expressionCollector;
    protected ASTNodeCollector astNodeCollector = new ASTNodeCollector();

    public void setSelect(Select select) {
        this.select = select;
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
    public void visit(PlainSelect plainSelect) {
        selectItems = plainSelect.getSelectItems();
        super.visit(plainSelect);
    }

    @Override
    public SelectStructuralData get() {
        if(selectItems != null) {
            return new SelectStructuralData(selectItems, expressionCollector.getBinaryExpressions(), select, astNodeCollector.getColumnNodeMap());
        }
        return null;
    }
}
