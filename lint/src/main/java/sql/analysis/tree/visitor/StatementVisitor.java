package sql.analysis.tree.visitor;

import sql.analysis.tree.visitor.delete.DeleteVisitor;
import sql.analysis.tree.visitor.insert.InsertVisitor;
import sql.analysis.tree.model.SQLStructuralData;
import sql.analysis.tree.visitor.select.SelectVisitor;
import sql.analysis.tree.visitor.update.UpdateVisitor;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.validation.validator.StatementValidator;

/**
 * This is the extended class of StatementValidator class
 * to reuse the well-defined validation procedure provided in StatementValidator class.<br><br>
 *
 * StatementValidator class is used in Validation.class which is the usual way to validate the sql statement
 * with capabilties like JdbcDatabaseMetaDataCapability.class.
 * <br><br>
 * It collects the data which is interesting for Diagnostics procedure.
 * <ul>
 *     <li>conditions in where clause</li>
 *     <li>columns in insert into(...)</li>
 *     <li>...</li>
 * </ul>
 */
public class StatementVisitor extends StatementValidator implements DefaultContextProvider {
    protected SelectVisitor selectVisitor = new SelectVisitor();
    protected InsertVisitor insertVisitor = new InsertVisitor();
    protected UpdateVisitor updateVisitor = new UpdateVisitor();
    protected DeleteVisitor deleteVisitor = new DeleteVisitor();

    private StatementType type = null;

    public SQLStructuralData getDetails() {
        if(type == null)
            return null;
        switch(type){
            case SELECT:
                return selectVisitor.get();
            case INSERT:
                return insertVisitor.get();
            case UPDATE:
                return updateVisitor.get();
            case DELETE:
                return deleteVisitor.get();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public StatementVisitor() {
        this.setContext(createValidationContext());
    }

    @Override
    public void visit(Select select) {
        selectVisitor.setSelect(select);
        validateFeature(Feature.select);

        if (select.getWithItemsList() != null) {
            select.getWithItemsList().forEach(wi -> wi.accept(selectVisitor));
        }
        select.getSelectBody().accept(selectVisitor);
        type = StatementType.SELECT;
    }

    @Override
    public void visit(Insert insert) {
        insertVisitor.validate(insert);
        type = StatementType.INSERT;
    }

    @Override
    public void visit(Update update) {
        updateVisitor.validate(update);
        type = StatementType.UPDATE;
    }

    @Override
    public void visit(Delete delete) {
        deleteVisitor.validate(delete);
        type = StatementType.DELETE;
    }

    private enum StatementType{
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }
}