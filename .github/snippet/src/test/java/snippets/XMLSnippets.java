package snippets;

import org.w3c.dom.Node;
import org.xml.sax.*;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author qwefgh90
 */
public class XMLSnippets {

    static public void stax(Path path) throws IOException, XMLStreamException {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        var reader = xmlif.createXMLEventReader(Files.newInputStream(path));
        while (reader.hasNext()) {
            var event = reader.nextEvent();
            if(event.isStartElement()){
                    System.out.println("isStartElement");
            }
//            if(reader.isAttributeSpecified(0)){
//                    System.out.println("isAttributeSpecified");
//            }
            if(event.isEndElement()){
                System.out.println("isEndElement");
            }
            if(event.isCharacters()){
                System.out.println("isCharacters");
                //TODO: modify the column number by counting characters again in a last line.

            }
            if(event.isStartDocument()){
                System.out.println("isStartDocument");
            }
            if(event.isEndDocument()){
                System.out.println("isEndDocument");
            }
            if(event.isAttribute()){
                System.out.println("isAttribute");
            }
//            switch (event) {
//                case XMLStreamConstants.START_ELEMENT:
//                    System.out.println("element");
//
////                    if ("employee".equals(reader.getLocalName())) {
////                        currEmp = new Employee();
////                        currEmp.setId(Integer.parseInt(reader.getAttributeValue(0)));
////                    }
////                    if ("employees".equals(reader.getLocalName())) {
////                        empList = new ArrayList<>();
////                    }
//                    break;
//                case XMLStreamConstants.ATTRIBUTE:
//                    System.out.println("element");
////                    tagContent = reader.getText().trim();
//                    break;
//
//                case XMLStreamConstants.END_ELEMENT:
//                    System.out.println("element");
////                    switch (reader.getLocalName()) {
////                        case "employee":
////                            empList.add(currEmp);
////                            break;
////                        case "name":
////                            currEmp.setName(tagContent);
////                            break;
////                        case "age":
////                            currEmp.setAge(Integer.parseInt(tagContent));
////                            break;
////                    }
//                    break;
//
//                case XMLStreamConstants.START_DOCUMENT:
//                    System.out.println("element");
////                    empList = new ArrayList<>();
//                    break;
//            }
        }

    }
    static public Node parseAndTransform(Path path) throws SAXException, TransformerException, FileNotFoundException {

        // The file to parse.

        /*
         * Create transformer SAX source that adds current element position to
         * the element as attributes.
         */
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        LocationFilter locationFilter = new LocationFilter(xmlReader);

        InputSource inputSource = new InputSource(new FileReader(path.toFile()));
// Do this so that XPath function document() can take relative URI.
        inputSource.setSystemId(path.getFileName().toString());
        SAXSource saxSource = new SAXSource(locationFilter, inputSource);

        /*
         * Perform an empty transformation from SAX source to DOM result.
         */
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMResult domResult = new DOMResult();
        transformer.transform(saxSource, domResult);
        Node root = domResult.getNode();
        return root;
    }
    static class LocationFilter extends XMLFilterImpl {

        LocationFilter(XMLReader xmlReader) {
            super(xmlReader);
        }

        private Locator locator = null;

        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // Add extra attribute to elements to hold location
            String location = locator.getSystemId() + ':' + locator.getLineNumber() + ':' + locator.getColumnNumber();
            Attributes2Impl attrs = new Attributes2Impl(attributes);
            attrs.addAttribute("http://myNamespace", "location", "myns:location", "CDATA", location);
            super.startElement(uri, localName, qName, attrs);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }
    }
}
