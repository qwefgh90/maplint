package mybatis.parser.sql;

import mybatis.parser.model.Config;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator;

import java.util.Map;
import java.util.Optional;

public class ForEachNode implements BaseSqlNode {
    public static final String ITEM_PREFIX = "__frch_";

    private final ExpressionEvaluator evaluator;
    private final String collectionExpression;
    private final Boolean nullable;
    private final BaseSqlNode contents;
    private final String open;
    private final String close;
    private final String separator;
    private final String item;
    private final String index;
    private final Config configuration;

//    /**
//     * @deprecated Since 3.5.9, use the {@link #ForEachNode(Configuration, BaseSqlNode, String, Boolean, String, String, String, String, String)}.
//     */
    @Deprecated
    public ForEachNode(Config configuration, BaseSqlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
        this(configuration, contents, collectionExpression, null, index, item, open, close, separator);
    }

    /**
     * @since 3.5.9
     */
    public ForEachNode(Config configuration, BaseSqlNode contents, String collectionExpression, Boolean nullable, String index, String item, String open, String close, String separator) {
        this.evaluator = new ExpressionEvaluator();
        this.collectionExpression = collectionExpression;
        this.nullable = nullable;
        this.contents = contents;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.index = index;
        this.item = item;
        this.configuration = configuration;
    }

    @Override
    public boolean apply(BaseSqlNodeVisitor context) {
        Map<String, Object> bindings = context.getBindings();
        final Iterable<?> iterable = evaluator.evaluateIterable(collectionExpression, bindings,
                Optional.ofNullable(nullable).orElseGet(configuration::isNullableOnForEach));
        if (iterable == null || !iterable.iterator().hasNext()) {
            return true;
        }
        boolean first = true;
        applyOpen(context);
        int i = 0;
        for (Object o : iterable) {
            BaseSqlNodeVisitor oldContext = context;
            if (first || separator == null) {
                context = new ForEachNode.PrefixedContext(context, "");
            } else {
                context = new ForEachNode.PrefixedContext(context, separator);
            }
            int uniqueNumber = context.getUniqueNumber();
            // Issue #709
            if (o instanceof Map.Entry) {
                @SuppressWarnings("unchecked")
                Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
                applyIndex(context, mapEntry.getKey(), uniqueNumber);
                applyItem(context, mapEntry.getValue(), uniqueNumber);
            } else {
                applyIndex(context, i, uniqueNumber);
                applyItem(context, o, uniqueNumber);
            }
            contents.apply(new ForEachNode.FilteredDynamicContext(configuration, context, index, item, uniqueNumber));
            if (first) {
                first = !((ForEachNode.PrefixedContext) context).isPrefixApplied();
            }
            context = oldContext;
            i++;
        }
        applyClose(context);
        context.getBindings().remove(item);
        context.getBindings().remove(index);
        return true;
    }

    private void applyIndex(BaseSqlNodeVisitor context, Object o, int i) {
        if (index != null) {
            context.bind(index, o);
            context.bind(itemizeItem(index, i), o);
        }
    }

    private void applyItem(BaseSqlNodeVisitor context, Object o, int i) {
        if (item != null) {
            context.bind(item, o);
            context.bind(itemizeItem(item, i), o);
        }
    }

    private void applyOpen(BaseSqlNodeVisitor context) {
        if (open != null) {
            context.appendSql(open);
        }
    }

    private void applyClose(BaseSqlNodeVisitor context) {
        if (close != null) {
            context.appendSql(close);
        }
    }

    private static String itemizeItem(String item, int i) {
        return ITEM_PREFIX + item + "_" + i;
    }

    private static class FilteredDynamicContext extends BaseSqlNodeVisitor {
        private final BaseSqlNodeVisitor delegate;
        private final int index;
        private final String itemIndex;
        private final String item;

        public FilteredDynamicContext(Config configuration, BaseSqlNodeVisitor delegate, String itemIndex, String item, int i) {
            super(configuration, null);
            this.delegate = delegate;
            this.index = i;
            this.itemIndex = itemIndex;
            this.item = item;
        }

        @Override
        public Map<String, Object> getBindings() {
            return delegate.getBindings();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public void appendSql(String sql) {
            GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
                String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
                if (itemIndex != null && newContent.equals(content)) {
                    newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
                }
                return "#{" + newContent + "}";
            });

            delegate.appendSql(parser.parse(sql));
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }

    }


    private class PrefixedContext extends BaseSqlNodeVisitor {
        private final BaseSqlNodeVisitor delegate;
        private final String prefix;
        private boolean prefixApplied;

        public PrefixedContext(BaseSqlNodeVisitor delegate, String prefix) {
            super(ForEachNode.this.configuration, null);
            this.delegate = delegate;
            this.prefix = prefix;
            this.prefixApplied = false;
        }

        public boolean isPrefixApplied() {
            return prefixApplied;
        }

        @Override
        public Map<String, Object> getBindings() {
            return delegate.getBindings();
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public void appendSql(String sql) {
            if (!prefixApplied && sql != null && sql.trim().length() > 0) {
                delegate.appendSql(prefix);
                prefixApplied = true;
            }
            delegate.appendSql(sql);
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public int getUniqueNumber() {
            return delegate.getUniqueNumber();
        }
    }

}
