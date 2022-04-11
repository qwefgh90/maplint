package db.mybatis;

import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

public class ParameterizedStatement {
    public final MappedStatement mappedStatement;
    public final java.sql.Statement statement;
    public final Statement ast;
    public final BoundSql boundSQL;
    public final String queryString;
    public final StatementHandler handler;

    public ParameterizedStatement(MappedStatement mappedStatement, java.sql.Statement statement, Statement ast, BoundSql boundSQL, String queryString, StatementHandler handler) {
        this.mappedStatement = mappedStatement;
        this.statement = statement;
        this.ast = ast;
        this.boundSQL = boundSQL;
        this.queryString = queryString;
        this.handler = handler;
    }
}
