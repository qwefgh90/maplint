package sql.analysis.base.select;

import sql.analysis.base.ModifiedAbstractValidator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.util.validation.ValidationCapability;

/**
 * @author qwefgh90
 */

public class ModifiedLimitValidator extends ModifiedAbstractValidator<Limit> {

    @Override
    public void validate(Limit limit) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.limit);
            validateFeature(c, limit.isLimitNull(), Feature.limitNull);
            validateFeature(c, limit.isLimitAll(), Feature.limitAll);
            validateOptionalFeature(c, limit.getOffset(), Feature.limitOffset);
        }
    }
}
