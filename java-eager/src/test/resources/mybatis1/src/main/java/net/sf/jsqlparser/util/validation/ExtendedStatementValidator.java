package net.sf.jsqlparser.util.validation;

import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.validation.validator.SelectValidator;
import net.sf.jsqlparser.util.validation.validator.StatementValidator;

public class ExtendedStatementValidator extends StatementValidator {

    @Override
    public void visit(Select select) {
        validateFeature(Feature.select);

        SelectValidator selectValidator = getValidator(ExtendedSelectValidator.class);
        if (select.getWithItemsList() != null) {
            select.getWithItemsList().forEach(wi -> wi.accept(selectValidator));
        }
        select.getSelectBody().accept(selectValidator);
    }
}
