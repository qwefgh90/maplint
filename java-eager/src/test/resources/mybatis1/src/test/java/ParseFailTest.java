import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class ParseFailTest {
    static Logger logger = LoggerFactory.getLogger(JSQLExampleTest.class);

    static SqlSession session;
    static Configuration conf;
    static Executor exec;
    static Transaction tran;
    static ParserUtils parserUtils;

    @BeforeAll
    static void before() {
        String resource = "config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        var s = new SessionFactory().getSession("");
//        new ClientPreparedQueryBindings(3, s);
//
//        new PreparedStatementHandler()
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);
        conf = parser.parse();
        var env = conf.getEnvironment();
        var transactionFactory = env.getTransactionFactory();
        tran = transactionFactory.newTransaction(env.getDataSource(), null, false);
        exec = conf.newExecutor(tran, ExecutorType.SIMPLE);
        parserUtils = new ParserUtils(conf, tran, exec);

        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream);
        session = sqlSessionFactory.openSession();
    }

    @AfterAll
    static void after() {
        session.close();
    }

    @Test
    void stmt1() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'\n" +
                        "order by Content.title descs"
        );
        logger.info("parsedStmt: " + errors.toString());
    }



    @Test
    void stmt2() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'd\n" +
                        "order by Content.title desc"
        );
        logger.info("parsedStmt: " + errors.toString());
    }

    @Test
    void stmt3() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name fromss Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "wheress name = 'sadf'\n" +
                        "order by Content.title desc"
        );
        logger.info("parsedStmt: " + errors.toString());
    }

    @Test
    void stmt4() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id,, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'\n" +
                        "order by Content.title desc"
        );
        logger.info("parsedStmt: " + errors.toString());
    }

    @Test
    void stmt5() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select 'sadfvsd' b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'\n" +
                        "order by Content.title desc"
        );
        logger.info("parsedStmt: " + errors.toString());
    }

    @Test
    void stmt6() throws SQLException, JSQLParserException {
        var parse = parserUtils.parse(
                "insert asntso Blog(name,category,owner,update_time) \n" +
                        "values ('asd', 3, 'sdf', '')"
        );
        logger.info("parse: " + parse.toString());

        var errors = parserUtils.validate(
                "insert asntso Blog(name,category,owner,update_time) \n" +
                        "values ('asd', 3, 'sdf', '')"
        );
        logger.info("errors: " + errors.toString());
    }

    @Test
    void stmt7() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "insert into Blog(name,category,owner,update_time) \n" +
                        "values ('asd', 3, concat(), 3 ,) "
        );
        logger.info("errors: " + errors.toString());
    }
}


