package mybatis.parser.sql;

import mybatis.parser.BaseParser;
import mybatis.parser.model.Config;
import mybatis.parser.sql.bound.BoundSqlStatementSource;
import mybatis.parser.sql.bound.DynamicBoundSqlStatementSource;
import mybatis.parser.sql.bound.RawBoundSqlStatementSource;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.parsing.XNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLSQLTextParser extends BaseParser {

    private final XNode context;
    private boolean isDynamic;
    private final String parameterType;
    private final Map<String, XMLSQLTextParser.NodeHandler> nodeHandlerMap = new HashMap<>();

    public XMLSQLTextParser(Config configuration, XNode context) {
        this(configuration, context, null);
    }

    public XMLSQLTextParser(Config configuration, XNode context, String parameterType) {
        super(configuration);
        this.context = context;
        this.parameterType = parameterType;
        initNodeHandlerMap();
    }

    private void initNodeHandlerMap() {
        nodeHandlerMap.put("trim", new XMLSQLTextParser.TrimHandler());
        nodeHandlerMap.put("where", new XMLSQLTextParser.WhereHandler());
        nodeHandlerMap.put("set", new XMLSQLTextParser.SetHandler());
        nodeHandlerMap.put("foreach", new XMLSQLTextParser.ForEachHandler());
        nodeHandlerMap.put("if", new XMLSQLTextParser.IfHandler());
        nodeHandlerMap.put("choose", new XMLSQLTextParser.ChooseHandler());
        nodeHandlerMap.put("when", new XMLSQLTextParser.IfHandler());
        nodeHandlerMap.put("otherwise", new XMLSQLTextParser.OtherwiseHandler());
        nodeHandlerMap.put("bind", new XMLSQLTextParser.BindHandler());
    }

    public BoundSqlStatementSource parseScriptNode() {
        MixedNode rootSqlNode = parseDynamicTags(context);
        BoundSqlStatementSource sqlSource;
        if (isDynamic) {
            sqlSource = new DynamicBoundSqlStatementSource(configuration, rootSqlNode);
        } else {
            sqlSource = new RawBoundSqlStatementSource(configuration, rootSqlNode, parameterType);
        }
        return sqlSource;
    }

    protected MixedNode parseDynamicTags(XNode node) {
        List<BaseSqlNode> contents = new ArrayList<>();
        NodeList children = node.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            XNode child = node.newXNode(children.item(i));
            if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
                String data = child.getStringBody("");
                TextNode textSqlNode = new TextNode(data);
                if (textSqlNode.isDynamic()) {
                    contents.add(textSqlNode);
                    isDynamic = true;
                } else {
                    contents.add(new StaticTextNode(data));
                }
            } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) { // issue #628
                String nodeName = child.getNode().getNodeName();
                XMLSQLTextParser.NodeHandler handler = nodeHandlerMap.get(nodeName);
                if (handler == null) {
                    throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
                }
                handler.handleNode(child, contents);
                isDynamic = true;
            }
        }
        return new MixedNode(contents);
    }

    private interface NodeHandler {
        void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents);
    }

    private class BindHandler implements XMLSQLTextParser.NodeHandler {
        public BindHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            final String name = nodeToHandle.getStringAttribute("name");
            final String expression = nodeToHandle.getStringAttribute("value");
            final VarDeclNode node = new VarDeclNode(name, expression);
            targetContents.add(node);
        }
    }

    private class TrimHandler implements XMLSQLTextParser.NodeHandler {
        public TrimHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            MixedNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            String prefix = nodeToHandle.getStringAttribute("prefix");
            String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
            String suffix = nodeToHandle.getStringAttribute("suffix");
            String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
            TrimNode trim = new TrimNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
            targetContents.add(trim);
        }
    }

    private class WhereHandler implements XMLSQLTextParser.NodeHandler {
        public WhereHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            MixedNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            WhereNode where = new WhereNode(configuration, mixedSqlNode);
            targetContents.add(where);
        }
    }

    private class SetHandler implements XMLSQLTextParser.NodeHandler {
        public SetHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            MixedNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            SetNode set = new SetNode(configuration, mixedSqlNode);
            targetContents.add(set);
        }
    }

    private class ForEachHandler implements XMLSQLTextParser.NodeHandler {
        public ForEachHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            MixedNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            String collection = nodeToHandle.getStringAttribute("collection");
            Boolean nullable = nodeToHandle.getBooleanAttribute("nullable");
            String item = nodeToHandle.getStringAttribute("item");
            String index = nodeToHandle.getStringAttribute("index");
            String open = nodeToHandle.getStringAttribute("open");
            String close = nodeToHandle.getStringAttribute("close");
            String separator = nodeToHandle.getStringAttribute("separator");
            ForEachNode forEachSqlNode = new ForEachNode(configuration, mixedSqlNode, collection, nullable, index, item, open, close, separator);
            targetContents.add(forEachSqlNode);
        }
    }

    private class IfHandler implements XMLSQLTextParser.NodeHandler {
        public IfHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            MixedNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            String test = nodeToHandle.getStringAttribute("test");
            IfNode ifSqlNode = new IfNode(mixedSqlNode, test);
            targetContents.add(ifSqlNode);
        }
    }

    private class OtherwiseHandler implements XMLSQLTextParser.NodeHandler {
        public OtherwiseHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            MixedNode mixedSqlNode = parseDynamicTags(nodeToHandle);
            targetContents.add(mixedSqlNode);
        }
    }

    private class ChooseHandler implements XMLSQLTextParser.NodeHandler {
        public ChooseHandler() {
            // Prevent Synthetic Access
        }

        @Override
        public void handleNode(XNode nodeToHandle, List<BaseSqlNode> targetContents) {
            List<BaseSqlNode> whenSqlNodes = new ArrayList<>();
            List<BaseSqlNode> otherwiseSqlNodes = new ArrayList<>();
            handleWhenOtherwiseNodes(nodeToHandle, whenSqlNodes, otherwiseSqlNodes);
            BaseSqlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
            ChooseNode chooseSqlNode = new ChooseNode(whenSqlNodes, defaultSqlNode);
            targetContents.add(chooseSqlNode);
        }

        private void handleWhenOtherwiseNodes(XNode chooseSqlNode, List<BaseSqlNode> ifSqlNodes, List<BaseSqlNode> defaultSqlNodes) {
            List<XNode> children = chooseSqlNode.getChildren();
            for (XNode child : children) {
                String nodeName = child.getNode().getNodeName();
                XMLSQLTextParser.NodeHandler handler = nodeHandlerMap.get(nodeName);
                if (handler instanceof XMLSQLTextParser.IfHandler) {
                    handler.handleNode(child, ifSqlNodes);
                } else if (handler instanceof XMLSQLTextParser.OtherwiseHandler) {
                    handler.handleNode(child, defaultSqlNodes);
                }
            }
        }

        private BaseSqlNode getDefaultSqlNode(List<BaseSqlNode> defaultSqlNodes) {
            BaseSqlNode defaultSqlNode = null;
            if (defaultSqlNodes.size() == 1) {
                defaultSqlNode = defaultSqlNodes.get(0);
            } else if (defaultSqlNodes.size() > 1) {
                throw new BuilderException("Too many default (otherwise) elements in choose statement.");
            }
            return defaultSqlNode;
        }
    }


}
