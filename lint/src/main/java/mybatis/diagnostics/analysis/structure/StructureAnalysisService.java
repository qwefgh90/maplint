package mybatis.diagnostics.analysis.structure;

import mybatis.diagnostics.analysis.structure.visitor.StatementVisitor;
import mybatis.diagnostics.analysis.structure.visitor.StatementSymbolSet;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * @author qwefgh90
 */
public class StructureAnalysisService {
    public static Statement parseStatement(String executableSql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(executableSql);
    }

    public static StatementSymbolSet getSymbol(Statement stmt) {
        var collector = new StatementVisitor();
        stmt.accept(collector);
        return collector.getDetails();
    }

}
