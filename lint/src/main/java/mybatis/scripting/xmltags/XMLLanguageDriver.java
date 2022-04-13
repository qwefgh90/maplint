package mybatis.scripting.xmltags;

import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.sql.TextNode;
import mybatis.parser.sql.XMLSQLTextParser;
import mybatis.parser.sql.bound.BoundSqlStatementSource;
import mybatis.parser.sql.bound.DynamicBoundSqlStatementSource;
import mybatis.parser.sql.bound.RawBoundSqlStatementSource;
import mybatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;

public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MapperStatement mappedStatement, Object parameterObject, BoundSqlStatement boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public BoundSqlStatementSource createSqlSource(Config configuration, XNode script, String parameterType) {
        XMLSQLTextParser builder = new XMLSQLTextParser(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    @Override
    public BoundSqlStatementSource createSqlSource(Config configuration, String script, String parameterType) {
        // issue #3
        if (script.startsWith("<script>")) {
            XPathParser parser = new XPathParser(script, false, configuration.getVariables(), new XMLMapperEntityResolver());
            return createSqlSource(configuration, parser.evalNode("/script"), parameterType);
        } else {
            // issue #127
            script = PropertyParser.parse(script, configuration.getVariables());
            TextNode textSqlNode = new TextNode(script);
            if (textSqlNode.isDynamic()) {
                return new DynamicBoundSqlStatementSource(configuration, textSqlNode);
            } else {
                return new RawBoundSqlStatementSource(configuration, script, parameterType);
            }
        }
    }

}
