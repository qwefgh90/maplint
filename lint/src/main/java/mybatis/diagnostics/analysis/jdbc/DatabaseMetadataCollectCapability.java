package mybatis.diagnostics.analysis.jdbc;

import mybatis.diagnostics.analysis.jdbc.model.NamedJDBCType;
import mybatis.diagnostics.analysis.base.AdditionalContextKey;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.util.validation.UnexpectedValidationException;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.ValidationException;
import net.sf.jsqlparser.util.validation.metadata.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DatabaseMetadataCollectCapability extends JdbcDatabaseMetaDataCapability {
    private static final Logger LOG = Logger.getLogger(DatabaseMetadataCollectCapability.class.getName());
    protected Map<Named, NamedJDBCType> columnTypeMap = new HashMap<>();
    protected Map<Named, List<ASTNodeAccess>> columnNodeMap = new HashMap<>();

    public DatabaseMetadataCollectCapability(Connection connection, UnaryOperator<String> namesLookup) {
        super(connection, namesLookup, true);
    }

    public Map<Named, Boolean> getExistMap(){
        return Collections.unmodifiableMap(this.results);
    }

    public Map<Named, List<ASTNodeAccess>> getColumnNodeMap() {
        return columnNodeMap;
    }

    public Map<Named, NamedJDBCType> getColumnTypeMap() {
        return columnTypeMap;
    }

    @Override
    public void validate(ValidationContext context, Consumer<ValidationException> errorConsumer) {
        Named named = context.get(MetadataContext.named, Named.class);
        var node = context.get(AdditionalContextKey.SimpleNode, ASTNodeAccess.class);
        columnNodeMap.computeIfAbsent(named, (key) -> new ArrayList<>()).add(node);
        exists(named);
    }

    /**
     * It's a modified copy from the parent class.
     * @param results
     * @param named
     * @return
     * @throws ValidationException
     */
    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity"})
    protected boolean columnExists(Map<Named, Boolean> results, Named named) throws ValidationException {
        String[] names = splitAndValidateMinMax(COLUMN, named.getFqnLookup(), 1, 4);
        String columnName = names[names.length - 1];

        List<String> possibleParentFqn = null;
        List<NamedObject> parentNamedObject = named.getParents().isEmpty() ? Arrays.asList(NamedObject.table)
                : named.getParents();

        int lastIndexOf = named.getFqnLookup().lastIndexOf(".");
        String parentFqn = lastIndexOf != -1 ? named.getFqnLookup().substring(0, lastIndexOf) : null;

        // try to match parents in results
        Predicate<? super Named> isParent = null;
        if (parentFqn != null) {
            isParent = n -> parentNamedObject.contains(n.getNamedObject())
                    && (parentFqn.equals(n.getAliasLookup()) || parentFqn.equals(n.getFqnLookup()));
        } else {
            isParent = n -> parentNamedObject.contains(n.getNamedObject());
        }
        possibleParentFqn = results.keySet().stream().filter(isParent).map(Named::getFqnLookup)
                .collect(Collectors.toList());

        if (possibleParentFqn.isEmpty()) {
            possibleParentFqn = Collections.singletonList(parentFqn);
        }

        for (String fqn : possibleParentFqn) {
            if (existsFromItem(results, fqn)) {
                String query = String.format("SELECT * FROM %s", fqn);
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ResultSetMetaData metaData = ps.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                            var typeName = metaData.getColumnTypeName(i);
                            var type = metaData.getColumnType(i);
                            if(typeName != null)
                                columnTypeMap.putIfAbsent(named, new NamedJDBCType(typeName, type));
                            else
                                LOG.warning(String.format("A type of %s hasn't been found. But it exists.", named.toString()));
                            return true;
                        }
                    }
                } catch (SQLException e) {
                    throw createDatabaseException(fqn, COLUMN, e);
                }
            } else if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(String.format("%s does not exists, cannot evaluate COLUMN from %s", fqn, named.getFqn()));
            }
        }
        return false;
    }

    //There are copies from the parent class below
    private boolean existsFromItem(Map<Named, Boolean> results, String fqn) {
        Named named = new Named(NamedObject.table, fqn).setFqnLookup(fqn);
        return viewExists(results, named) || tableExists(results, named);
    }
    private static final String COLUMN = "COLUMN";
    /**
     * Split fqn by "." and validate expected path-elements
     *
     * @param type
     * @param fqn
     * @param min
     * @param max
     * @return the fqn-parts
     */
    private String[] splitAndValidateMinMax(String type, String fqn, int min, int max) {
        String[] names = fqn.split("\\.");
        if (names.length < min || names.length > max) {
            throw new UnexpectedValidationException(String.format(
                    "%s path-elements count needs to be between %s and %s for %s", fqn, min, max, type));
        }
        return names;
    }
    private DatabaseException createDatabaseException(String fqn, String type, SQLException e) {
        return new DatabaseException(String.format(
                "cannot evaluate existence of %s by name '%s'", type, fqn), e);
    }
}
