package lint.cli.option;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author qwefgh90
 */
public class DataSource {
    Pair<String, String> authPair;
    String jdbcUrl;
    DatabaseKind databaseKind;

    public Pair<String, String> getAuthPair() {
        return authPair;
    }

    public void setAuthPair(Pair<String, String> authPair) {
        this.authPair = authPair;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public DatabaseKind getDatabaseKind() {
        return databaseKind;
    }

    public void setDatabaseKind(DatabaseKind databaseKind) {
        this.databaseKind = databaseKind;
    }

    public static final class DataSourceBuilder {
        Pair<String, String> authPair;
        String jdbcUrl;
        DatabaseKind databaseKind;

        private DataSourceBuilder() {
        }

        public static DataSourceBuilder aDataSource() {
            return new DataSourceBuilder();
        }

        public DataSourceBuilder authPair(Pair<String, String> authPair) {
            this.authPair = authPair;
            return this;
        }

        public DataSourceBuilder jdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
            return this;
        }

        public DataSourceBuilder databaseKind(DatabaseKind databaseKind) {
            this.databaseKind = databaseKind;
            return this;
        }

        public DataSource build() {
            DataSource dataSource = new DataSource();
            dataSource.setAuthPair(authPair);
            dataSource.setJdbcUrl(jdbcUrl);
            dataSource.setDatabaseKind(databaseKind);
            return dataSource;
        }
    }
}
