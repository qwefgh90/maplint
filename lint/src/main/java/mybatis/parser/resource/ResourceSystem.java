package mybatis.parser.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface ResourceSystem {
    InputStream getResourceAsStream(String resource) throws IOException;
    Properties getUrlAsProperties(String urlString) throws IOException;
    Properties getResourceAsProperties(String resource) throws IOException;

}