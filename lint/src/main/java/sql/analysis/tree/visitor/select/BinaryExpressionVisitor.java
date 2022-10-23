package sql.analysis.tree.visitor.select;

import sql.analysis.tree.visitor.DefaultContextProvider;
import sql.analysis.base.select.ModifiedExpressionValidator;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.*;

import java.util.ArrayList;
import java.util.List;

public class BinaryExpressionVisitor extends ModifiedExpressionValidator implements DefaultContextProvider {
    public BinaryExpressionVisitor() {
//        this.setContext(createValidationContext());
    }
    protected List<BinaryExpression> binaryExpressions = new ArrayList<>();

    public List<BinaryExpression> getBinaryExpressions() {
        return binaryExpressions;
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        this.binaryExpressions.add(equalsTo);
        super.visit(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        this.binaryExpressions.add(greaterThan);
        super.visit(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        this.binaryExpressions.add(greaterThanEquals);
        super.visit(greaterThanEquals);
    }

    @Override
    public void visit(MinorThan minorThan) {
        this.binaryExpressions.add(minorThan);
        super.visit(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        this.binaryExpressions.add(minorThanEquals);
        super.visit(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        this.binaryExpressions.add(notEqualsTo);
        super.visit(notEqualsTo);
    }


}
