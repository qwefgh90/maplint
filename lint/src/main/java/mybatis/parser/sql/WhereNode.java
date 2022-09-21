package mybatis.parser.sql;

import mybatis.parser.model.Config;

import java.util.Arrays;
import java.util.List;

public class WhereNode extends TrimNode {

    private static List<String> prefixList = Arrays.asList("AND ","OR ","AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

    public WhereNode(Config configuration, SqlNode contents) {
        super(configuration, contents, "WHERE", prefixList, null, null);
    }
}
