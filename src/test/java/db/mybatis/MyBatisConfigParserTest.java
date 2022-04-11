package db.mybatis;

import db.mybatis.config.parser.MyBatisConfigParser;
import org.apache.ibatis.session.ExecutorType;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class MyBatisConfigParserTest {

    @Test
    void test(){
        MyBatisConfigParser.parse(this.getClass().getClassLoader().getResourceAsStream("config1/config.xml"));
    }

    @Test
    void test2(){
        var conf = MyBatisConfigParser.parse(this.getClass().getClassLoader().getResourceAsStream("config1/config.xml"));
//        var env = conf.getEnvironment();
//        var transactionFactory = env.getTransactionFactory();
//        tran = transactionFactory.newTransaction(env.getDataSource(), null, false);
//        exec = conf.newExecutor(tran, ExecutorType.SIMPLE);
//        parserUtils = new ParserUtils(conf, tran, exec);
//        var blog = conf.getMappedStatement("selectBlog");

    }
}
