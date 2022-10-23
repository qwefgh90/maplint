package mybatis.diagnostics.analysis.tree.expression;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;

/**
 * @author qwefgh90
 */
public class ColumnValueExpression extends BinaryExpression {

    private BinaryExpression binaryExpression;

    private Column column;
    private Expression literal;
    boolean leftColumn = false;

    protected ColumnValueExpression(BinaryExpression binaryExpression, boolean leftColumn) {
        this.binaryExpression = binaryExpression;
        if(leftColumn) {
            column = (Column) getLeftExpression();
            literal = getRightExpression();
        }else{
            literal = getLeftExpression();
            column = (Column)getRightExpression();
        }
        this.leftColumn = leftColumn;
    }

    public boolean isLeftColumn() {
        return leftColumn;
    }

    public Column getColumn() {
        return column;
    }

    public Expression getLiteral() {
        return literal;
    }

    @Override
    public String getStringExpression() {
        return binaryExpression.getStringExpression();
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        binaryExpression.accept(expressionVisitor);
    }

    @Override
    public Expression getLeftExpression() {
        return binaryExpression.getLeftExpression();
    }

    @Override
    public Expression getRightExpression() {
        return binaryExpression.getRightExpression();
    }

    @Override
    public BinaryExpression withLeftExpression(Expression expression) {
        return binaryExpression.withLeftExpression(expression);
    }

    @Override
    public void setLeftExpression(Expression expression) {
        binaryExpression.setLeftExpression(expression);
    }

    @Override
    public BinaryExpression withRightExpression(Expression expression) {
        return binaryExpression.withRightExpression(expression);
    }

    @Override
    public void setRightExpression(Expression expression) {
        binaryExpression.setRightExpression(expression);
    }

    @Override
    public String toString() {
        return binaryExpression.toString();
    }

    @Override
    public <E extends Expression> E getLeftExpression(Class<E> type) {
        return binaryExpression.getLeftExpression(type);
    }

    @Override
    public <E extends Expression> E getRightExpression(Class<E> type) {
        return binaryExpression.getRightExpression(type);
    }

    @Override
    public SimpleNode getASTNode() {
        return binaryExpression.getASTNode();
    }

    @Override
    public void setASTNode(SimpleNode node) {
        binaryExpression.setASTNode(node);
    }
}
