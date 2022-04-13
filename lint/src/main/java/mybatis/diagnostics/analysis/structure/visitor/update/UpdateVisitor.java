package mybatis.diagnostics.analysis.structure.visitor.update;

import mybatis.diagnostics.analysis.structure.visitor.ASTNodeCollector;
import mybatis.diagnostics.analysis.structure.visitor.DefaultContextProvider;
import mybatis.diagnostics.analysis.structure.visitor.select.BinaryExpressionVisitor;
import mybatis.diagnostics.analysis.base.select.ModifiedExpressionValidator;
import mybatis.diagnostics.analysis.base.update.ModifiedUpdateValidator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;

import java.util.List;
import java.util.function.Supplier;

public class UpdateVisitor extends ModifiedUpdateValidator implements DefaultContextProvider, Supplier<UpdateSymbolSet> {
    public UpdateVisitor() {
        this.setContext(createValidationContext(astNodeCollector));
        expressionCollector = getValidator(BinaryExpressionVisitor.class);
    }

    protected Update update;
    protected List<UpdateSet> updateSetList;
    protected BinaryExpressionVisitor expressionCollector;
    protected ASTNodeCollector astNodeCollector = new ASTNodeCollector();

    public List<UpdateSet> getUpdateSetList() {
        return updateSetList;
    }

    public BinaryExpressionVisitor getExpressionCollector() {
        return expressionCollector;
    }

    @Override
    protected void validateOptionalMultiExpressionList(MultiExpressionList multiExprList) {
        if (multiExprList != null) {
            ModifiedExpressionValidator v = expressionCollector;
            multiExprList.getExpressionLists().stream().map(ExpressionList::getExpressions).flatMap(List::stream)
                    .forEach(e -> e.accept(v));
        }
    }

    @Override
    protected void validateOptionalExpression(Expression expression) {
        validateOptional(expression, e -> e.accept(expressionCollector));
    }

    @Override
    protected void validateOptionalExpressions(List<? extends Expression> expressions) {
        validateOptionalList(expressions, () -> expressionCollector, (o, v) -> o.accept(v));
    }

    @Override
    public void validate(Update update) {
        this.update = update;
        updateSetList = update.getUpdateSets();
        super.validate(update);
    }

    @Override
    public UpdateSymbolSet get() {
        if(updateSetList != null)
            return new UpdateSymbolSet(updateSetList, expressionCollector.getBinaryExpressions(), update, astNodeCollector.getColumnNodeMap());
        return null;
    }
}
