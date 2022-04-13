package mybatis.diagnostics.analysis.structure.visitor.insert;

import mybatis.diagnostics.analysis.structure.visitor.ASTNodeCollector;
import mybatis.diagnostics.analysis.structure.visitor.DefaultContextProvider;
import mybatis.diagnostics.analysis.base.Insert.ModifiedInsertValidator;
import mybatis.diagnostics.analysis.structure.visitor.select.BinaryExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InsertVisitor extends ModifiedInsertValidator implements DefaultContextProvider, Supplier<InsertSymbolSet> {
    public InsertVisitor() {
        this.setContext(createValidationContext(astNodeCollector));
        itemListVisitor = getValidator(ItemListVisitor.class);
    }

    protected Insert insert;
    protected List<Pair<Column, Expression>> pairList;
    protected ItemListVisitor itemListVisitor;
    protected ASTNodeCollector astNodeCollector = new ASTNodeCollector();

    public ExpressionList getExpressionList(){
        return this.itemListVisitor.getExpressionList();
    }

    public List<Pair<Column, Expression>> getPairList() {
        return pairList;
    }

    @Override
    public void validate(Insert insert) {
        this.insert = insert;
        pairList = new ArrayList<>();
        var cols = insert.getColumns();
        if(insert.getItemsList() != null)
            insert.getItemsList().accept(itemListVisitor);
        if(getExpressionList() != null){
            var exps = getExpressionList().getExpressions();
            if (cols.size() == exps.size()) {
                for (int i = 0; i < cols.size(); i++) {
                    pairList.add(Pair.of(cols.get(i), exps.get(i)));
                }
            }
        }
        super.validate(insert);
    }

    @Override
    public InsertSymbolSet get() {
//        if(pairList != null && getExpressionList() != null){
            return new InsertSymbolSet(pairList, getExpressionList(), insert, astNodeCollector.getColumnNodeMap());
//        }
//        return null;
    }
}
