package sql.analysis.base.select;

import sql.analysis.base.ModifiedAbstractValidator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.util.validation.ValidationCapability;

/**
 * @author qwefgh90
 */
public class ModifiedOrderByValidator extends ModifiedAbstractValidator<OrderByElement> implements OrderByVisitor {

    @Override
    public void validate(OrderByElement element) {
        element.accept(this);
    }

    @Override
    public void visit(OrderByElement orderBy) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.orderBy);
            validateOptionalFeature(c, orderBy.getNullOrdering(), Feature.orderByNullOrdering);
        }
        getValidator(ModifiedExpressionValidator.class).validate(orderBy.getExpression());
    }

}
