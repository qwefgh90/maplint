package mybatis.parser.model;

import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class DataSourceConfig {
    private final Properties props;
    private final String type;
    private final DataSourceFactory dataSourceFactory;

    public DataSourceConfig(Properties props, String type, DataSourceFactory dataSource) {
        this.props = props;
        this.type = type;
        this.dataSourceFactory = dataSource;
    }

    public Properties getProps() {
        return props;
    }

    public String getType() {
        return type;
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public DataSource getDataSource(){
        return dataSourceFactory.getDataSource();
    }
}
