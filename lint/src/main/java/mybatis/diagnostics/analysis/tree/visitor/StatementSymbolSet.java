package mybatis.diagnostics.analysis.tree.visitor;

import mybatis.diagnostics.analysis.database.model.SourcePosition;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class StatementSymbolSet<T extends Statement> {
    protected T statement;
    protected Map<Named, List<ASTNodeAccess>> columnNodeMap;

    public Map<Named, List<ASTNodeAccess>> getColumnNodeMap() {
        return columnNodeMap;
    }

    public T getStatement() {
        return statement;
    }

    public StatementSymbolSet(T statement, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        this.statement = statement;
        this.columnNodeMap = columnNodeMap;
    }

    @Override
    public String toString() {
        return
                "columnNodeMap=\n" +
                        String.join("\n", columnNodeMap.entrySet().stream()
                                .map(e -> String.format("%s-%s", e.getKey(),
                                        String.join(",", e.getValue().stream()
                                                .map(node -> String.format("%s(%s)",
                                                        node.toString(),
                                                        SourcePosition.getSourcePosition(node))).collect(Collectors.toList()))

                                ))
                                .collect(Collectors.toList())
                        );
    }
}
