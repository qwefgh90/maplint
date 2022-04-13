package mybatis.parser;

import mybatis.parser.model.NotationToken;

import java.util.List;

/**
 * @author Clinton Begin
 */
public interface TokenHandler {
    String handleToken(String content, NotationToken notation, int index, List<String> uniqueIdList, String statement);
}

