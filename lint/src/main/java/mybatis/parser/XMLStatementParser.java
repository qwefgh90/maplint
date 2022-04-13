package mybatis.parser;

import mybatis.parser.keygen.Jdbc3KeyGenerator;
import mybatis.parser.keygen.KeyGenerator;
import mybatis.parser.keygen.NoKeyGenerator;
import mybatis.parser.model.BoundSqlStatement;
import mybatis.parser.model.Config;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.sql.XMLSQLTextParser;
import mybatis.parser.sql.bound.BoundSqlStatementSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.parsing.XNode;

import java.util.List;
import java.util.Locale;
//XMLStatementBuilder.java
public class XMLStatementParser extends BaseParser{
    private final MapperParserHelper mapperParserHelper;
    private final XNode context;
    private final String requiredDatabaseId;

    public XMLStatementParser(Config configuration, MapperParserHelper mapperParserHelper, XNode context) {
        this(configuration, mapperParserHelper, context, null);
    }

    public XMLStatementParser(Config configuration, MapperParserHelper mapperParserHelper, XNode context, String databaseId) {
        super(configuration);
        this.mapperParserHelper = mapperParserHelper;
        this.context = context;
        this.requiredDatabaseId = databaseId;
    }

    public void parseStatementNode() {
        String id = context.getStringAttribute("id");
        String databaseId = context.getStringAttribute("databaseId");

        if (!databaseIdMatchesCurrent(id, databaseId, this.requiredDatabaseId)) {
            return;
        }

        String nodeName = context.getNode().getNodeName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        boolean flushCache = context.getBooleanAttribute("flushCache", !isSelect);
        boolean useCache = context.getBooleanAttribute("useCache", isSelect);
        boolean resultOrdered = context.getBooleanAttribute("resultOrdered", false);

        // Include Fragments before parsing
        XMLIncludeHelper includeParser = new XMLIncludeHelper(configuration, mapperParserHelper);
        includeParser.applyIncludes(context.getNode());

        String parameterType = context.getStringAttribute("parameterType");
//        Class<?> parameterTypeClass = resolveClass(parameterType);

        String lang = context.getStringAttribute("lang");
        if(lang == null)
            lang = "org.apache.ibatis.scripting.xmltags.XMLLanguageDriver";
//        LanguageDriver langDriver = getLanguageDriver(lang);

        // Parse selectKey after includes and remove them.
        processSelectKeyNodes(id, parameterType, lang);

        // Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
        mybatis.parser.keygen.KeyGenerator keyGenerator;
        String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        keyStatementId = mapperParserHelper.applyCurrentNamespace(keyStatementId, true);
        if (configuration.hasKeyGenerator(keyStatementId)) {
            keyGenerator = configuration.getKeyGenerator(keyStatementId);
        } else {
            keyGenerator = context.getBooleanAttribute("useGeneratedKeys",
                    configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType))
                    ? Jdbc3KeyGenerator.INSTANCE : mybatis.parser.keygen.NoKeyGenerator.INSTANCE;
        }

        BoundSqlStatementSource sqlSource = new XMLSQLTextParser(configuration, context, parameterType).parseScriptNode();
        StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType", StatementType.PREPARED.toString()));
        Integer fetchSize = context.getIntAttribute("fetchSize");
        Integer timeout = context.getIntAttribute("timeout");
        String parameterMap = context.getStringAttribute("parameterMap");
        String resultType = context.getStringAttribute("resultType");
//        Class<?> resultTypeClass = resolveClass(resultType);
        String resultMap = context.getStringAttribute("resultMap");
        String resultSetType = context.getStringAttribute("resultSetType");
        ResultSetType resultSetTypeEnum = resolveResultSetType(resultSetType);
        if (resultSetTypeEnum == null) {
            resultSetTypeEnum = configuration.getDefaultResultSetType();
        }
        String keyProperty = context.getStringAttribute("keyProperty");
        String keyColumn = context.getStringAttribute("keyColumn");
        String resultSets = context.getStringAttribute("resultSets");

        mapperParserHelper.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
                fetchSize, timeout, parameterMap, parameterType, resultMap, resultType,
                resultSetTypeEnum, flushCache, useCache, resultOrdered,
                keyGenerator, keyProperty, keyColumn, databaseId, lang, resultSets, context.getStringBody());
    }

    private void processSelectKeyNodes(String id, String parameterType, String lang) {
        List<XNode> selectKeyNodes = context.evalNodes("selectKey");
        if (configuration.getDatabaseId() != null) {
            parseSelectKeyNodes(id, selectKeyNodes, parameterType, lang, configuration.getDatabaseId());
        }
        parseSelectKeyNodes(id, selectKeyNodes, parameterType, lang,null);
        removeSelectKeyNodes(selectKeyNodes);
    }

    private void parseSelectKeyNodes(String parentId, List<XNode> list, String parameterType, String lang, String skRequiredDatabaseId) {
        for (XNode nodeToHandle : list) {
            String id = parentId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            String databaseId = nodeToHandle.getStringAttribute("databaseId");
            if (databaseIdMatchesCurrent(id, databaseId, skRequiredDatabaseId)) {
                parseSelectKeyNode(id, nodeToHandle, parameterType, lang, databaseId);
            }
        }
    }

    private void parseSelectKeyNode(String id, XNode nodeToHandle, String parameterType, String lang, String databaseId) {
        String resultType = nodeToHandle.getStringAttribute("resultType");
//        Class<?> resultTypeClass = resolveClass(resultType);
        StatementType statementType = StatementType.valueOf(nodeToHandle.getStringAttribute("statementType", StatementType.PREPARED.toString()));
        String keyProperty = nodeToHandle.getStringAttribute("keyProperty");
        String keyColumn = nodeToHandle.getStringAttribute("keyColumn");
        boolean executeBefore = "BEFORE".equals(nodeToHandle.getStringAttribute("order", "AFTER"));

        // defaults
        boolean useCache = false;
        boolean resultOrdered = false;
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        Integer fetchSize = null;
        Integer timeout = null;
        boolean flushCache = false;
        String parameterMap = null;
        String resultMap = null;
        ResultSetType resultSetTypeEnum = null;

        BoundSqlStatementSource sqlSource = new XMLSQLTextParser(configuration, nodeToHandle, parameterType).parseScriptNode();
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        mapperParserHelper.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
                fetchSize, timeout, parameterMap, parameterType, resultMap, resultType,
                resultSetTypeEnum, flushCache, useCache, resultOrdered,
                keyGenerator, keyProperty, keyColumn, databaseId, lang, null, null);

        id = mapperParserHelper.applyCurrentNamespace(id, false);

        MapperStatement keyStatement = configuration.getMappedStatement(id, false);
        configuration.addKeyGenerator(id, new mybatis.parser.keygen.SelectKeyGenerator(keyStatement, executeBefore));
    }

    private void removeSelectKeyNodes(List<XNode> selectKeyNodes) {
        for (XNode nodeToHandle : selectKeyNodes) {
            nodeToHandle.getParent().getNode().removeChild(nodeToHandle.getNode());
        }
    }

    private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
        if (requiredDatabaseId != null) {
            return requiredDatabaseId.equals(databaseId);
        }
        if (databaseId != null) {
            return false;
        }
        id = mapperParserHelper.applyCurrentNamespace(id, false);
        if (!this.configuration.hasStatement(id, false)) {
            return true;
        }
        // skip this statement if there is a previous one with a not null databaseId
        MapperStatement previous = this.configuration.getMappedStatement(id, false); // issue #2
        return previous.getDatabaseId() == null;
    }
}
