package mybatis.util;

import mybatis.diagnostics.analysis.jdbc.model.SourcePosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author qwefgh90
 */
public class MapperUtil {
    public static String trimSignature(String executableSql){
        var index = executableSql.lastIndexOf("\n--");
        if(index == -1)
            return executableSql;
        var query = executableSql.substring(0, index);
        return query;
    }
    public static List<String> getParameterIdList(String executableSql){
        var arr = executableSql.split("\n--");
        if(arr.length == 1)
            return Collections.emptyList();
        var signature = arr[arr.length - 1];
        return Arrays.asList(signature.split(","));
    }

}
