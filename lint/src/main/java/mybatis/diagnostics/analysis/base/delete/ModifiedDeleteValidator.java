package mybatis.diagnostics.analysis.base.delete;

import mybatis.diagnostics.analysis.base.ModifiedAbstractValidator;
import mybatis.diagnostics.analysis.base.select.ModifiedLimitValidator;
import mybatis.diagnostics.analysis.base.select.ModifiedSelectValidator;
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
