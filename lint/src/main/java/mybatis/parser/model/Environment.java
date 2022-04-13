package mybatis.parser.model;

public class Environment {
    private final DataSourceConfig dataSourceConfig;
    private final TransactionManager transactionManager;
    private final String id;

    public Environment(DataSourceConfig dataSourceConfig, TransactionManager transactionManager, String id) {
        this.dataSourceConfig = dataSourceConfig;
        this.transactionManager = transactionManager;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
