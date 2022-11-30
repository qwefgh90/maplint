import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import snippets.XMLSnippets;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author qwefgh90
 */
public class Run {
    @Test
    public void xmlWithLC() throws URISyntaxException, IOException, TransformerException, SAXException, XPathExpressionException, XMLStreamException {
        var path = Paths.get(Run.class.getClassLoader().getResource("./Test.xml").toURI());
        XMLSnippets.stax(path);
    }
}
