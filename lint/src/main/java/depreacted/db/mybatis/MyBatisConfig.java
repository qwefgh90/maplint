package depreacted.db.mybatis;

import depreacted.db.mybatis.exception.MyBatisConfigParsingException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;

public abstract class MyBatisConfig {
    private final Configuration conf;
    private final Environment env;

    public MyBatisConfig(Configuration conf, Environment env){
        this.conf = conf;
        this.env = env;
    }

    public abstract ParameterizedStatement createStatement(String id, Object param) throws MyBatisConfigParsingException;

    public Configuration getConf() {
        return conf;
    }

    public Environment getEnv() {
        return env;
    }
}