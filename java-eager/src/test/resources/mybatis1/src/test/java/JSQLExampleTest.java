import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.util.validation.feature.DatabaseType;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class JSQLExampleTest {
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

    /**
     * 'into' is optional in some databases
     * https://stackoverflow.com/questions/5399087/sql-server-insert-without-into
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void optionalIntoKeyword() throws SQLException, JSQLParserException {
        var parseStatement = parserUtils.parse(
                "insert Blog (name,category,owner,update_time)\n" +
                        "values ('blogName', 1, 'ccw', '2022-03-31 22:16:03.180626')"
        );
        logger.info("parsedStmt: " + parseStatement.toString());
    }

    /**
     * It's not running on MySQL, But It is accepted.
     * @throws SQLException
     */
    @Test
    void syntaxErrorOnValidator() throws SQLException {
        var errors = parserUtils.validate(
                "insert Blog Blog (name,category,owner,update_time)\n" +
                        "values ('blogName', 1, 'ccw', '2022-03-31 22:16:03.180626')"
                , DatabaseType.MYSQL
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 0);
        //The wrong result!
    }

    /**
     * The type of the error is wrong, but it's right that an error occurs
     * @throws SQLException
     */
    @Test
    void semanticErrorOnValidator() throws SQLException {
        var errors = parserUtils.validate(
                "insert asntso Blog(name,category,owner,update_time) \n"+
                        "values ('asd', 3, 'sdf', '', '')"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
        //The wrong type!
    }

    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void correctQuery() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select nested.name from Blog as nested where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where b.name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 0);
    }
    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongTableName() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content12341234 ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }

    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongTableName2() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog12341234 as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }

    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongReferenceTableName() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select asdfavsadv.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }

    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongReferenceTableName2() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content12341234.blog_id \n" +
                        "where name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }

    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongReferenceTableName3() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where Cotent.name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }

    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongColumn() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.idqwdqwd, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }
    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongColumn2() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id12341234 \n" +
                        "where name = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }
    /**
     * @throws SQLException
     * @throws JSQLParserException
     */
    @Test
    void wrongColumn3() throws SQLException, JSQLParserException {
        var errors = parserUtils.validate(
                "select b.id, name\n" +
                        ",(select name from Blog where name = 'sadf') as name2\n" +
                        ", category, owner\n" +
                        ", b.update_time from Blog as b \n" +
                        "left join Content ON b.id = Content.blog_id \n" +
                        "where name12341234 = 'sadf'"
                , parserUtils.newExtendedCapability()
        );
        logger.info("errors: " + errors.toString());
        Assertions.assertEquals(errors.size(), 1);
    }

    @Test
    void example1() throws SQLException, JSQLParserException {
        var capa = parserUtils.newExtendedCapability();
        var errors = parserUtils.validate(
                "insert into Blog(name,category,owner,update_time) \n"+
                        "values ('asd', 3, concat(), 3 ) "
                ,capa
        );
        logger.info("errors: " + errors.toString());
        logger.info("capa: " + capa.metaDataMap.toString());
        capa.colIndexMap.forEach((name, action) -> {
            logger.info(name.toString());
            try {
                logger.info(capa.metaDataMap.get(name).getColumnName(action));
                logger.info(capa.metaDataMap.get(name).getColumnTypeName(action));
                logger.info(capa.metaDataMap.get(name).getColumnLabel(action));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        logger.info("indexes: " + capa.colIndexMap.toString());
    }

    @Test
    void example2() throws SQLException {
        var capa = parserUtils.newExtendedCapability();
        var errors = parserUtils.validate(
                "select b.id, name ,(select name from Blog where name = 'sadf') as name2 \n" +
                        ", category, owner, b.update_time \n" +
                        "from Blog as b \n" +
                        "left join Content\n" +
                        "ON b.id = Content.blog_id  \n" +
                        "where name = 'sadf' order by Content.title desc"
                ,capa
        );
        logger.info("errors: " + errors.toString());
        logger.info("capa: " + capa.metaDataMap.toString());
        capa.colIndexMap.forEach((name, action) -> {
            logger.info(name.toString());
            try {
                logger.info(capa.metaDataMap.get(name).getColumnName(action));
                logger.info(capa.metaDataMap.get(name).getColumnTypeName(action));
                logger.info(capa.metaDataMap.get(name).getTableName(action));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
