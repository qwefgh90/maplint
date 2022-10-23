package sql.analysis;

import sql.analysis.base.Insert.ModifiedInsertValidator;
import sql.analysis.base.delete.ModifiedDeleteValidator;
import sql.analysis.base.select.ModifiedSelectValidator;
import sql.analysis.base.update.ModifiedUpdateValidator;
import sql.analysis.database.capability.DatabaseMetadataCollector;
import sql.analysis.database.model.SchemeObjectSnapshot;
import sql.analysis.tree.model.SQLStructuralData;
import sql.analysis.tree.visitor.StatementVisitor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.feature.FeatureConfiguration;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.metadata.NamesLookup;

import java.sql.Connection;
import java.util.Arrays;

public class SQLAnalysisService {
    public static SchemeObjectSnapshot analyze(Connection connection, String executableSql) throws JSQLParserException {
        var statement = parseStatement(executableSql);
        var databaseMetadataCollector = new DatabaseMetadataCollector(connection, NamesLookup.UPPERCASE);
        ValidationContext context = new ValidationContext();
        context.setCapabilities(Arrays.asList(databaseMetadataCollector));
        context.setConfiguration(new FeatureConfiguration());
        if(statement instanceof Select) {
            var select = (Select)statement;
            var validator = new ModifiedSelectValidator();
            validator.setContext(context);
            validator.validate(select);
        }else if(statement instanceof Insert){
            var insert = (Insert)statement;
            var validator = new ModifiedInsertValidator();
            validator.setContext(context);
            validator.validate(insert);
        }else if(statement instanceof Update){
            var update = (Update)statement;
            var validator = new ModifiedUpdateValidator();
            validator.setContext(context);
            validator.validate(update);
        }else if(statement instanceof Delete){
            var delete = (Delete)statement;
            var validator = new ModifiedDeleteValidator();
            validator.setContext(context);
            validator.validate(delete);
        }else{
            throw new UnsupportedOperationException();
        }
        return new SchemeObjectSnapshot(databaseMetadataCollector.getColumnToTypeMap(), databaseMetadataCollector.getExistMap());
    }

    /**
     * Return a set of symbols
     * @param stmt
     * @return
     */
    public static SQLStructuralData getSymbolSet(Statement stmt) {
        var collector = new StatementVisitor();
        stmt.accept(collector);
        return collector.getDetails();
    }

    /**
     * Return a set of symbols
     * @param query
     * @return
     */
    public static SQLStructuralData getSymbolSet(String query) throws JSQLParserException {
        return getSymbolSet(parseStatement(query));
    }

    /**
     * Parse the query with JSQLParser
     * @param executableSql
     * @return
     * @throws JSQLParserException
     */
    public static Statement parseStatement(String executableSql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(executableSql);
    }
}
