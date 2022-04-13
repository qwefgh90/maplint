package mybatis.diagnostics.analysis.base.select;

import mybatis.diagnostics.analysis.base.ModifiedAbstractValidator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.values.ValuesStatement;

/**
 * @author qwefgh90
 */
public class ModifiedValuesStatementValidator extends ModifiedAbstractValidator<ValuesStatement> {

    @Override
    public void validate(ValuesStatement values) {
        validateFeature(Feature.values);
        validateOptionalItemsList(values.getExpressions());
    }
}
