package sql.analysis.tree.model;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.util.validation.metadata.Named;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class InsertStructuralData extends SQLStructuralData<Insert> {

    public final List<Pair<Column, Expression>> pairs;
    public final ExpressionList values;

    public InsertStructuralData(List<Pair<Column, Expression>> pairs, ExpressionList expressionList, Insert insert, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        super(insert, columnNodeMap);
        this.pairs = pairs;
        this.values = expressionList;
    }

    @Override
    public String toString() {
        return "InsertSymbolSet{" +
                "pairs=" + pairs +
                ", values=" + values +
                ", \nStatementSymbolSet=\n" + super.toString() +
                '}';
    }
}
