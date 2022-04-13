import net.sf.jsqlparser.util.validation.UnexpectedValidationException;
import net.sf.jsqlparser.util.validation.ValidationException;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import net.sf.jsqlparser.util.validation.metadata.JdbcDatabaseMetaDataCapability;
import net.sf.jsqlparser.util.validation.metadata.Named;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExtendedJdbcDatabaseMetaDataCapability extends JdbcDatabaseMetaDataCapability {

    public ExtendedJdbcDatabaseMetaDataCapability(Connection connection, UnaryOperator<String> namesLookup) {
        super(connection, namesLookup);
    }

    private static final String VIEW = "VIEW";
    private static final String TABLE = "TABLE";
    private static final String COLUMN = "COLUMN";
    private static final Logger LOG = Logger.getLogger(JdbcDatabaseMetaDataCapability.class.getName());
    public HashMap<Named, ResultSetMetaData> metaDataMap = new HashMap<>();
    public HashMap<Named, Integer> colIndexMap = new HashMap<>();

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity"})
    protected boolean columnExists(Map<Named, Boolean> results, Named named) throws ValidationException {
        String[] names = splitAndValidateMinMax(COLUMN, named.getFqnLookup(), 1, 4);
        String columnName = names[names.length - 1];

        List<String> possibleParents = null;
        List<NamedObject> parents = named.getParents().isEmpty() ? Arrays.asList(NamedObject.table)
                : named.getParents();

        int lastIndexOf = named.getFqnLookup().lastIndexOf(".");
        String fqnParent = lastIndexOf != -1 ? named.getFqnLookup().substring(0, lastIndexOf) : null;

        // try to match parents in results
        Predicate<? super Named> predicate = null;
        if (fqnParent != null) {
            predicate = n -> parents.contains(n.getNamedObject())
                    && (fqnParent.equals(n.getAliasLookup()) || fqnParent.equals(n.getFqnLookup()));
        } else {
            predicate = n -> parents.contains(n.getNamedObject());
        }
        possibleParents = results.keySet().stream().filter(predicate).map(Named::getFqnLookup)
                .collect(Collectors.toList());

        if (possibleParents.isEmpty()) {
            possibleParents = Collections.singletonList(fqnParent);
        }

        for (String fqn : possibleParents) {
            if (existsFromItem(results, fqn)) {
                String query = String.format("SELECT * FROM %s", fqn);
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ResultSetMetaData metaData = ps.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                            this.colIndexMap.put(named, i);
                            this.metaDataMap.put(named, metaData);
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

    private boolean existsFromItem(Map<Named, Boolean> results, String fqn) {
        Named named = new Named(NamedObject.table, fqn).setFqnLookup(fqn);
        return viewExists(results, named) || tableExists(results, named);
    }
}
