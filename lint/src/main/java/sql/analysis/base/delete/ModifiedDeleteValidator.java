package sql.analysis.base.delete;

import sql.analysis.base.ModifiedAbstractValidator;
import sql.analysis.base.select.ModifiedLimitValidator;
import sql.analysis.base.select.ModifiedSelectValidator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.util.validation.ValidationCapability;

/**
 * @author qwefgh90
 */
public class ModifiedDeleteValidator extends ModifiedAbstractValidator<Delete> {


    @Override
    public void validate(Delete delete) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.delete);

            validateOptionalFeature(c, delete.getTables(), Feature.deleteTables);
            validateOptionalFeature(c, delete.getJoins(), Feature.deleteJoin);
            validateOptionalFeature(c, delete.getLimit(), Feature.deleteLimit);
            validateOptionalFeature(c, delete.getOrderByElements(), Feature.deleteOrderBy);
        }

        ModifiedSelectValidator v = getValidator(ModifiedSelectValidator.class);
        delete.getTable().accept(v);

        if (isNotEmpty(delete.getTables())) {
            delete.getTables().forEach(t -> t.accept(v));
        }

        validateOptionalExpression(delete.getWhere());
        validateOptionalOrderByElements(delete.getOrderByElements());

        v.validateOptionalJoins(delete.getJoins());

        if (delete.getLimit() != null) {
            getValidator(ModifiedLimitValidator.class).validate(delete.getLimit());
        }

    }

}
