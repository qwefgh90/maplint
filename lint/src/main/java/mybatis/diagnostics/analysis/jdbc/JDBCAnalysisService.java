package mybatis.diagnostics.analysis.jdbc;

import mybatis.diagnostics.analysis.jdbc.model.JDBCMetadata;
import mybatis.diagnostics.analysis.structure.StructureAnalysisService;
import mybatis.diagnostics.analysis.base.Insert.ModifiedInsertValidator;
import mybatis.diagnostics.analysis.base.delete.ModifiedDeleteValidator;
import mybatis.diagnostics.analysis.base.select.ModifiedSelectValidator;
import mybatis.diagnostics.analysis.base.update.ModifiedUpdateValidator;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.feature.FeatureConfiguration;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.metadata.NamesLookup;

import java.sql.Connection;
import java.util.Arrays;

/**
 * @author qwefgh90
 */
public class JDBCAnalysisService {
    public static JDBCMetadata getMetadata(Connection connection, String executableSql) throws JSQLParserException {
        var statement = StructureAnalysisService.parseStatement(executableSql);
        var databaseMetadataCollectCapability = new DatabaseMetadataCollectCapability(connection, NamesLookup.UPPERCASE);
        ValidationContext context = new ValidationContext();
        context.setCapabilities(Arrays.asList(databaseMetadataCollectCapability));
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
        }
        return new JDBCMetadata(databaseMetadataCollectCapability.getColumnTypeMap(), databaseMetadataCollectCapability.getExistMap(), databaseMetadataCollectCapability.getColumnNodeMap());
    }
}
