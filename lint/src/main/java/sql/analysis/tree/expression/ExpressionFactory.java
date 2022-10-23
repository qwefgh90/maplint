package sql.analysis.tree.expression;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;

/**
 * @author qwefgh90
 */
public class ExpressionFactory {
    public static BinaryExpression wrapExpression(BinaryExpression expression) {
        if (expression.getLeftExpression() instanceof Column
                && isLiteral(expression.getRightExpression())) {
            return new ColumnValueExpression(expression, true);
        } else if (expression.getRightExpression() instanceof Column
                && isLiteral(expression.getLeftExpression())) {
            return new ColumnValueExpression(expression, false);
        }
        return expression;
    }

    public static boolean isLiteral(Expression expression){
        return (expression instanceof StringValue ||
                expression instanceof LongValue ||
//                expression instanceof AllValue ||
                expression instanceof DateValue ||
                expression instanceof DoubleValue ||
//                expression instanceof HexValue ||
//                expression instanceof NullValue ||
                expression instanceof TimeValue
//                expression instanceof ValueListExpression
        );
    }

}
