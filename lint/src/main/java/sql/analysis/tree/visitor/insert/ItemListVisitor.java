package sql.analysis.tree.visitor.insert;

import sql.analysis.base.select.ModifiedItemsListValidator;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class ItemListVisitor extends ModifiedItemsListValidator {

    protected ExpressionList expressionList;

    @Override
    public void visit(ExpressionList expressionList) {
        this.expressionList = expressionList;
        validateOptionalExpressions(expressionList.getExpressions());
    }

    public ExpressionList getExpressionList() {
        return expressionList;
    }
}
