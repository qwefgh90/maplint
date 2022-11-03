package misc;

import mybatis.parser.XMLConfigParser;
import mybatis.parser.location.LocationAnnotator;
import mybatis.parser.location.LocationData;
import mybatis.parser.model.Config;
import mybatis.project.ConfigNotFoundException;
import mybatis.project.MyBatisProjectInitializationException;
import mybatis.project.MyBatisProjectService;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;

public class SaxParsingTest {
    Logger logger = LoggerFactory.getLogger(SaxParsingTest.class);
    static Config config;
    @BeforeAll
    static void setup() throws ConfigNotFoundException, IOException, URISyntaxException, SQLException, MyBatisProjectInitializationException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1").toURI()).normalize();
        var server = new MyBatisProjectService(root, "h2");
        var path = server.getConfigFile();

        var parser = new XMLConfigParser(Files.newInputStream(path), server);
        config = parser.parse();
    }

    @Test
    void saxParsingTest() throws URISyntaxException, ParserConfigurationException, TransformerException, SAXException {
        var root = Paths.get(ClassLoader.getSystemClassLoader().getResource("examples/mybatis-app1/src/main/resources/db/BlogMapper.xml").toURI()).normalize();
        saxParsing(root.toAbsolutePath().toString());
    }

    private void saxParsing(String path) throws TransformerException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory documentBuilderFactory
                = DocumentBuilderFactory.newInstance();
        TransformerFactory transformerFactory
                = TransformerFactory.newInstance();
        Transformer nullTransformer
                = transformerFactory.newTransformer();

        /*
         * Create an empty document to be populated within a DOMResult.
         */
        DocumentBuilder docBuilder
                = documentBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        DOMResult domResult = new DOMResult(doc);

        /*
         * Create SAX parser/XMLReader that will parse XML. If factory
         * options are not required then this can be short cut by:
         *      xmlReader = XMLReaderFactory.createXMLReader();
         */
        SAXParserFactory saxParserFactory
                = SAXParserFactory.newInstance();
        // saxParserFactory.setNamespaceAware(true);
        // saxParserFactory.setValidating(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        /*
         * Create our filter to wrap the SAX parser, that captures the
         * locations of elements and annotates their nodes as they are
         * inserted into the DOM.
         */
        LocationAnnotator locationAnnotator
                = new LocationAnnotator(xmlReader, doc);

        /*
         * Create the SAXSource to use the annotator.
         */
//        String systemId = new File("example.xml").getAbsolutePath();
        InputSource inputSource = new InputSource(path);
        SAXSource saxSource
                = new SAXSource(locationAnnotator, inputSource);

        /*
         * Finally read the XML into the DOM.
         */
        nullTransformer.transform(saxSource, domResult);

        /*
         * Find one of the element nodes in our DOM and output the location
         * information.
         */
        Node n = doc.getElementsByTagName("mapper").item(0).getFirstChild();
        LocationData locationData = (LocationData)
                n.getUserData(LocationData.LOCATION_DATA_KEY);
        System.out.println(locationData);
    }

    @Nested
    @DisplayName("Collect")
    class PlaceholderTest {
        @Test
        void e1() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithAmbiguousPlaceHolder2").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);
            var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
            var stmt = list.get(0);
            var select = (Select)stmt;
        }

        @Test
        void e2() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.WrongPosition.selectContentByBlogWithAmbiguousPlaceHolder").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);
            var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
            var stmt = list.get(0);
            var select = (Select)stmt;
        }
        @Test
        void e3() throws IOException, ConfigNotFoundException, URISyntaxException, SQLException, JSQLParserException {
            var executableSql = config.getMappedStatement("db.BlogMapper.selectContentByBlog").getBoundSql(new HashMap<>()).toString();
            logger.debug(executableSql);
            var list = CCJSqlParserUtil.parseStatements(executableSql).getStatements();
            var stmt = list.get(0);
            var select = (Select)stmt;
        }
    }
}