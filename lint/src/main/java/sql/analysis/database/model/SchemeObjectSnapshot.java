package sql.analysis.database.model;

import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author qwefgh90
 */
public class SchemeObjectSnapshot {
    public SchemeObjectSnapshot(Map<Named, NamedJDBCType> columnToTypeMap, Map<Named, Boolean> results) {
        this.columnToTypeMap = columnToTypeMap;
        this.columnExistMap = results;
    }

    protected Map<Named, NamedJDBCType> columnToTypeMap;
    protected Map<Named, Boolean> columnExistMap;

    /**
     * The fqn value equals to the literal in the SQL statement
     * @return
     */
    public Map<Named, Boolean> getColumnExistMap(){
        return Collections.unmodifiableMap(this.columnExistMap);
    }
    public Map<Named, NamedJDBCType> getColumnToTypeMap() {
        return columnToTypeMap;
    }

    /**
     * Return the type of the named object
     * @param named
     * @return
     */
    public NamedJDBCType getType(Named named){
        return columnToTypeMap.get(named);
    }

    /**
     * Check if the named object exists
     * @param named
     * @return
     */
    public Boolean exists(Named named){
        return columnExistMap.getOrDefault(named, false);
    }

    @Override
    public String toString() {
        return "QueryAnalysisResult{" +
                "\ncolumnToTypeMap=\n" + String.join("\n", columnToTypeMap.entrySet().stream()
                        .map(s -> s.toString()).collect(Collectors.toList())) +
                "\ncolumnExistMap=\n" + String.join("\n", columnExistMap.entrySet().stream()
                .map(s -> s.toString()).collect(Collectors.toList())) +
                '}';
    }
}
