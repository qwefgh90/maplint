package mybatis.diagnostics.analysis.base.select;

import mybatis.diagnostics.analysis.base.ModifiedAbstractValidator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.GroupByVisitor;
import net.sf.jsqlparser.util.validation.ValidationCapability;

/**
 * @author qwefgh90
 */
public class ModifiedGroupByValidator extends ModifiedAbstractValidator<GroupByElement> implements GroupByVisitor {

    @Override
    public void validate(GroupByElement groupBy) {
        groupBy.accept(this);
    }

    @Override
    public void visit(GroupByElement groupBy) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.selectGroupBy);
            if (isNotEmpty(groupBy.getGroupingSets())) {
                validateFeature(c, Feature.selectGroupByGroupingSets);
            }
        }

        validateOptionalExpressions(groupBy.getGroupByExpressions());

        if (isNotEmpty(groupBy.getGroupingSets())) {
            for (Object o : groupBy.getGroupingSets()) {
                if (o instanceof Expression) {
                    validateOptionalExpression((Expression) o);
                } else if (o instanceof ExpressionList) {
                    validateOptionalExpressions(((ExpressionList) o).getExpressions());
                }
            }
        }
    }

}
