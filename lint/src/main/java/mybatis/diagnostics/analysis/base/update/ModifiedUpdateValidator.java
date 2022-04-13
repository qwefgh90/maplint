package mybatis.diagnostics.analysis.base.update;

import mybatis.diagnostics.analysis.base.ModifiedAbstractValidator;
import mybatis.diagnostics.analysis.base.select.ModifiedLimitValidator;
import mybatis.diagnostics.analysis.base.select.ModifiedSelectValidator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.validation.ValidationCapability;

import java.util.stream.Collectors;

/**
 * @author qwefgh90
 */
public class ModifiedUpdateValidator extends ModifiedAbstractValidator<Update> {
    @Override
    public void validate(Update update) {

        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.update);
            validateOptionalFeature(c, update.getFromItem(), Feature.updateFrom);
            validateOptionalFeature(c, update.getStartJoins(), Feature.updateJoins);
            validateFeature(c, update.isUseSelect(), Feature.updateUseSelect);
            validateOptionalFeature(c, update.getOrderByElements(), Feature.updateOrderBy);
            validateOptionalFeature(c, update.getLimit(), Feature.updateLimit);
            if (isNotEmpty(update.getReturningExpressionList()) || update.isReturningAllColumns()) {
                validateFeature(c, Feature.updateReturning);
            }
        }

        validateOptionalFromItem(update.getTable());

        validateOptional(update.getStartJoins(),
                j -> getValidator(ModifiedSelectValidator.class).validateOptionalJoins(j));

        if (update.isUseSelect()) {
            validateOptionalExpressions(update.getColumns());
            validateOptional(update.getSelect(), e -> e.getSelectBody().accept(getValidator(ModifiedSelectValidator.class)));
        } else {
            validateOptionalExpressions(update.getColumns());
            validateOptionalExpressions(update.getExpressions());
        }

        if (update.getFromItem() != null) {
            validateOptionalFromItem(update.getFromItem());
            validateOptional(update.getJoins(),
                    j -> getValidator(ModifiedSelectValidator.class).validateOptionalJoins(j));
        }

        validateOptionalExpression(update.getWhere());
        validateOptionalOrderByElements(update.getOrderByElements());

        if (update.getLimit() != null) {
            getValidator(ModifiedLimitValidator.class).validate(update.getLimit());
        }

        if (update.getReturningExpressionList() != null) {
            validateOptionalExpressions(update.getReturningExpressionList().stream()
                    .map(SelectExpressionItem::getExpression).collect(Collectors.toList()));
        }
    }

}
