package mybatis.diagnostics.analysis.tree.visitor.insert;

import mybatis.diagnostics.analysis.base.select.ModifiedItemsListValidator;
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
