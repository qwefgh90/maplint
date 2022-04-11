package db.mybatis.config.parser;

import db.mybatis.MyBatisConfig;
import db.mybatis.config.parser.xml.ExternalXMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;

import java.io.InputStream;

public abstract class MyBatisConfigParser {
    public static MyBatisConfig parse(InputStream inputStream){
        ExternalXMLConfigBuilder parser = new ExternalXMLConfigBuilder(inputStream, null, null);
        var conf = parser.parse();
        var env = conf.getEnvironment();
        return new DefaultMyBatisConfig(conf, env);
    }
}
