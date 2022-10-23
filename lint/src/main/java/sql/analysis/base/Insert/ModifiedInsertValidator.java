package sql.analysis.base.Insert;

import sql.analysis.base.ModifiedAbstractValidator;
import sql.analysis.base.select.ModifiedExpressionValidator;
import sql.analysis.base.select.ModifiedSelectValidator;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.util.validation.ValidationCapability;

/**
 * @author qwefgh90
 */
public class ModifiedInsertValidator extends ModifiedAbstractValidator<Insert> {


    @Override
    public void validate(Insert insert) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.insert);
            validateOptionalFeature(c, insert.getItemsList(), Feature.insertValues);
            validateOptionalFeature(c, insert.getModifierPriority(), Feature.insertModifierPriority);
            validateFeature(c, insert.isModifierIgnore(), Feature.insertModifierIgnore);
            validateOptionalFeature(c, insert.getSelect(), Feature.insertFromSelect);
            validateFeature(c, insert.isUseSet(), Feature.insertUseSet);
            validateFeature(c, insert.isUseDuplicate(), Feature.insertUseDuplicateKeyUpdate);
            validateFeature(c, insert.isReturningAllColumns(), Feature.insertReturningAll);
            validateOptionalFeature(c, insert.getReturningExpressionList(), Feature.insertReturningExpressionList);
        }

        validateOptionalFromItem(insert.getTable());
        validateOptionalExpressions(insert.getColumns());
        validateOptionalItemsList(insert.getItemsList());

        if (insert.getSelect() != null) {
            // It means that there is no modified version of StatementValidator.
//            insert.getSelect().accept(getValidator(StatementValidator.class));
            var select = insert.getSelect();
            validateFeature(Feature.select);
            ModifiedSelectValidator selectValidator = getValidator(ModifiedSelectValidator.class);
            if (select.getWithItemsList() != null) {
                select.getWithItemsList().forEach(wi -> wi.accept(selectValidator));
            }
            select.getSelectBody().accept(selectValidator);
        }

        if (insert.isUseSet()) {
            ModifiedExpressionValidator v = getValidator(ModifiedExpressionValidator.class);
            // TODO is this useful?
            // validateModelCondition (insert.getSetColumns().size() !=
            // insert.getSetExpressionList().size(), "model-error");
            insert.getSetColumns().forEach(c -> c.accept(v));
            insert.getSetExpressionList().forEach(c -> c.accept(v));
        }

        if (insert.isUseDuplicate()) {
            ModifiedExpressionValidator v = getValidator(ModifiedExpressionValidator.class);
            // TODO is this useful?
            // validateModelCondition (insert.getDuplicateUpdateColumns().size() !=
            // insert.getDuplicateUpdateExpressionList().size(), "model-error");
            insert.getDuplicateUpdateColumns().forEach(c -> c.accept(v));
            insert.getDuplicateUpdateExpressionList().forEach(c -> c.accept(v));
        }

        if (isNotEmpty(insert.getReturningExpressionList())) {
            ModifiedExpressionValidator v = getValidator(ModifiedExpressionValidator.class);
            insert.getReturningExpressionList().forEach(c -> c.getExpression().accept(v));
        }
    }

}
