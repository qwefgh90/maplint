package mybatis.parser.model;

import org.apache.ibatis.transaction.TransactionFactory;

import java.util.Properties;

public class TransactionManager {
    private final Properties props;
    private final String type;
    private final TransactionFactory transactionFactory;

    public TransactionManager(Properties props, String type, TransactionFactory transactionFactory) {
        this.props = props;
        this.type = type;
        this.transactionFactory = transactionFactory;
    }

    public Properties getProps() {
        return props;
    }

    public String getType() {
        return type;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }
}
