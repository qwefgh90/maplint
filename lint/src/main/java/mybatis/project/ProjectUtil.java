package mybatis.project;

import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XPathParser;

import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectUtil {
    public static boolean isMyBatisConfigFile(Path path){
        try {
            var parser = new XPathParser(Files.newInputStream(path), true, null, new XMLMapperEntityResolver());
            if((parser.evalNode("configuration") != null)
                    && (parser.evalNode("configuration").evalNode("mappers") != null))
                return true;
            return false;
        }catch(Exception e){
            return false;
        }
    }
}
