package mybatis.parser.sql;

import mybatis.parser.model.Config;

import java.util.Collections;
import java.util.List;

public class SetNode extends TrimNode{


    private static final List<String> COMMA = Collections.singletonList(",");

    public SetNode(Config configuration, SqlNode contents) {
        super(configuration, contents, "SET", COMMA, null, COMMA);
    }
}
