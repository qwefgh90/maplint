package mybatis.diagnostics.analysis.tree;

import mybatis.diagnostics.analysis.tree.visitor.StatementVisitor;
import mybatis.diagnostics.analysis.tree.visitor.StatementSymbolSet;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * @author qwefgh90
 */
public class StructureAnalysisService {
    /**
     * Parse the query with JSQLParser
     * @param executableSql
     * @return
     * @throws JSQLParserException
     */
    public static Statement parseStatement(String executableSql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(executableSql);
    }

    /**
     * Return a set of symbols
     * @param stmt
     * @return
     */
    public static StatementSymbolSet getSymbolSet(Statement stmt) {
        var collector = new StatementVisitor();
        stmt.accept(collector);
        return collector.getDetails();
    }

    /**
     * Return a set of symbols
     * @param query
     * @return
     */
    public static StatementSymbolSet getSymbolSet(String query) throws JSQLParserException {
        return getSymbolSet(parseStatement(query));
    }

}
