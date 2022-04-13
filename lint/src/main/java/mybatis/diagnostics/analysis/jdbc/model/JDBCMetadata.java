package mybatis.diagnostics.analysis.jdbc.model;

import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qwefgh90
 */
public class JDBCMetadata {
    public JDBCMetadata(Map<Named, NamedJDBCType> objectTypeMap, Map<Named, Boolean> results, Map<Named, List<ASTNodeAccess>> columnNodeMap) {
        this.columnTypeMap = objectTypeMap;
        this.columnExistMap = results;
        this.columnNodeMap = columnNodeMap;
    }

    protected Map<Named, NamedJDBCType> columnTypeMap;
    protected Map<Named, Boolean> columnExistMap;
    //Named Object can be mapped to multiple AST nodes.
    protected Map<Named, List<ASTNodeAccess>> columnNodeMap;

    /**
     * The fqn value equals to the literal in the SQL statement
     * @return
     */
    public Map<Named, Boolean> getColumnExistMap(){
        return Collections.unmodifiableMap(this.columnExistMap);
    }
    public Map<Named, NamedJDBCType> getColumnTypeMap() {
        return columnTypeMap;
    }

    @Override
    public String toString() {
        return "Metadata{"
                +"\ncolumnTypeMap=\n"
                + String.join("\n", columnTypeMap.entrySet().stream()
                        .map(s -> s.toString()).collect(Collectors.toList()))
                +", \ncolumnExistMap=\n"
                + String.join("\n", columnExistMap.entrySet().stream()
                        .map(s -> s.toString()).collect(Collectors.toList())) +
                '}';
    }
}
