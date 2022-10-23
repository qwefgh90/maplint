package mybatis.diagnostics.analysis.tree.visitor.update;

import mybatis.diagnostics.analysis.tree.expression.ExpressionFactory;
import mybatis.diagnostics.analysis.tree.visitor.StatementSymbolSet;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import net.sf.jsqlparser.util.validation.metadata.Named;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateSymbolSet extends StatementSymbolSet<Update> {
    Logger logger = LoggerFactory.getLogger(UpdateSymbolSet.class);
    public final List<UpdateSet> updateSetList;
    public final List<Pair<Column, Expression>> simpleSetPairs;
    public final List<BinaryExpression> binaryExpressionsExceptForSet;

    public UpdateSymbolSet(List<UpdateSet> updateSetList, List<BinaryExpression> binaryExpressionsInWhere, Update update, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        super(update, columnNodeMap);
        this.updateSetList = updateSetList;
        this.binaryExpressionsExceptForSet = binaryExpressionsInWhere.stream().map(b -> ExpressionFactory.wrapExpression(b)).collect(Collectors.toList());
        var pairs = new ArrayList<Pair<Column, Expression>>();

        for(var set : updateSetList){
            if(set.getColumns().size() == 1 && set.getExpressions().size() == 1){
                var col = set.getColumns().get(0);
                var val = set.getExpressions().get(0);
                pairs.add(Pair.of(col, val));
            }else
                logger.warn("The number of column or value is greater than one. {}", set);
        }
        this.simpleSetPairs = Collections.unmodifiableList(pairs);
    }

    @Override
    public String toString() {
        return "UpdateSymbolSet{" +
                "updateSetList=" + updateSetList +
                ", simpleSetPairs=" + simpleSetPairs +
                ", binaryExpressionsExceptForSet=" + binaryExpressionsExceptForSet +
                ", \nStatementSymbolSet=\n" + super.toString() +
                '}';
    }
}
