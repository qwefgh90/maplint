package sql.analysis.tree.model;

import sql.analysis.tree.expression.ExpressionFactory;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectStructuralData extends SQLStructuralData<Select> {
    public final List<SelectExpressionItem> selectItems;
    public final List<BinaryExpression> binaryExpressions;

    public SelectStructuralData(List<SelectItem> selectItems, List<BinaryExpression> binaryExpressions, Select select, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        super(select, columnNodeMap);
        this.selectItems = selectItems
                .stream()
                .filter(s -> s instanceof SelectExpressionItem)
                .map(s -> (SelectExpressionItem) s)
                .collect(Collectors.toList());
        this.binaryExpressions = binaryExpressions.stream()
                .map(b -> ExpressionFactory.wrapExpression(b))
                .collect(Collectors.toList());

    }

    @Override
    public String toString() {
        return "SelectSymbolSet{" +
                "selectItems=" + selectItems +
                ", binaryExpressions=" + binaryExpressions +
                ", \nStatementSymbolSet=\n" + super.toString() +
                '}';
    }
}
