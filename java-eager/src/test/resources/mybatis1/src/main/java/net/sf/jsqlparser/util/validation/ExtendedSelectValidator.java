package net.sf.jsqlparser.util.validation;

import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.validator.GroupByValidator;
import net.sf.jsqlparser.util.validation.validator.LimitValidator;
import net.sf.jsqlparser.util.validation.validator.SelectValidator;

public class ExtendedSelectValidator extends SelectValidator {

    @Override
    public void visit(PlainSelect plainSelect) {

        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.select);
            validateFeature(c, plainSelect.getMySqlHintStraightJoin(), Feature.mySqlHintStraightJoin);
            validateOptionalFeature(c, plainSelect.getOracleHint(), Feature.oracleHint);
            validateOptionalFeature(c, plainSelect.getSkip(), Feature.skip);
            validateOptionalFeature(c, plainSelect.getFirst(), Feature.first);

            if (plainSelect.getDistinct() != null) {
                if (plainSelect.getDistinct().isUseUnique()) {
                    validateFeature(c, Feature.selectUnique);
                } else {
                    validateFeature(c, Feature.distinct);
                }
                validateOptionalFeature(c, plainSelect.getDistinct().getOnSelectItems(), Feature.distinctOn);
            }

            validateOptionalFeature(c, plainSelect.getTop(), Feature.top);
//            validateFeature(c, plainSelect.getMySqlSqlNoCache(), Feature.mysqlSqlNoCache);
            validateFeature(c, plainSelect.getMySqlSqlCalcFoundRows(), Feature.mysqlCalcFoundRows);
            validateOptionalFeature(c, plainSelect.getIntoTables(), Feature.selectInto);
            validateOptionalFeature(c, plainSelect.getKsqlWindow(), Feature.kSqlWindow);
            validateFeature(c, isNotEmpty(plainSelect.getOrderByElements()) && plainSelect.isOracleSiblings(),
                    Feature.oracleOrderBySiblings);

            if (plainSelect.isForUpdate()) {
                validateFeature(c, Feature.selectForUpdate);
                validateOptionalFeature(c, plainSelect.getForUpdateTable(), Feature.selectForUpdateOfTable);
                validateOptionalFeature(c, plainSelect.getWait(), Feature.selectForUpdateWait);
                validateFeature(c, plainSelect.isNoWait(), Feature.selectForUpdateNoWait);
            }

            validateOptionalFeature(c, plainSelect.getForXmlPath(), Feature.selectForXmlPath);
            validateOptionalFeature(c, plainSelect.getOptimizeFor(), Feature.optimizeFor);
        } // end for

        // Comment a wrong line. (1)
        //  validateOptionalList(plainSelect.getSelectItems(), () -> this, (e, v) -> e.accept(v));

        validateOptionalFromItem(plainSelect.getFromItem());
        validateOptionalFromItems(plainSelect.getIntoTables());
        validateOptionalJoins(plainSelect.getJoins());

        // Moved here (2)
        validateOptionalList(plainSelect.getSelectItems(), () -> this, (e, v) -> e.accept(v));

        validateOptionalExpression(plainSelect.getWhere());
        validateOptionalExpression(plainSelect.getOracleHierarchical());

        if (plainSelect.getGroupBy() != null) {
            plainSelect.getGroupBy().accept(getValidator(GroupByValidator.class));
        }

        validateOptionalExpression(plainSelect.getHaving());
        validateOptionalOrderByElements(plainSelect.getOrderByElements());

        if (plainSelect.getLimit() != null) {
            getValidator(LimitValidator.class).validate(plainSelect.getLimit());
        }

        if (plainSelect.getOffset() != null) {
            validateOffset(plainSelect.getOffset());
        }

        if (plainSelect.getFetch() != null) {
            validateFetch(plainSelect.getFetch());
        }

    }
}
