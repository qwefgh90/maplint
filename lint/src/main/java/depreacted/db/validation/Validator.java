package depreacted.db.validation;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.metadata.JdbcDatabaseMetaDataCapability;
import net.sf.jsqlparser.util.validation.metadata.NamesLookup;

import java.sql.Connection;
import java.util.*;

public class Validator {
    private final String stmt;
    public Validator(String stmt){
        this.stmt = stmt;
    }

    public Statement parse() throws JSQLParserException {
        return CCJSqlParserUtil.parse(stmt);
    }

    public List<ValidationError> validate(Connection conn) {
        var capability = new JdbcDatabaseMetaDataCapability(conn, NamesLookup.NO_TRANSFORMATION);
        Validation validation = new Validation(Arrays.asList(capability), stmt);
        List<ValidationError> errors = validation.validate();
        return errors;
    }
}
