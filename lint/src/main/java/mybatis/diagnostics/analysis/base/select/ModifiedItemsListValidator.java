package mybatis.diagnostics.analysis.base.select;

import mybatis.diagnostics.analysis.base.ModifiedAbstractValidator;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * @author qwefgh90
 */
public class ModifiedItemsListValidator extends ModifiedAbstractValidator<ItemsList> implements ItemsListVisitor {

    @Override
    public void visit(SubSelect subSelect) {
        validateOptionalFromItem(subSelect);
    }

    @Override
    public void visit(ExpressionList expressionList) {
        validateOptionalExpressions(expressionList.getExpressions());
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {
        validateOptionalExpressions(namedExpressionList.getExpressions());
    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
        multiExprList.getExpressionLists().forEach(l -> l.accept(this));
    }

    @Override
    public void validate(ItemsList statement) {
        statement.accept(this);
    }

}
