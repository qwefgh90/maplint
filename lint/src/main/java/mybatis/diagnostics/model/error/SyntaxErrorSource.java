package mybatis.diagnostics.model.error;

import net.sf.jsqlparser.JSQLParserException;

import java.util.regex.Pattern;

public final class SyntaxErrorSource {
    //TODO: column number is based on a bound statement
    public final int line;
    public final int column;
    public final JSQLParserException exception;
    public SyntaxErrorSource(JSQLParserException exception) {
        String[] lines = exception.getMessage().split("\n");
        var pattern = "line (\\d+), column (\\d+)";
        var matcher = Pattern.compile(pattern).matcher(lines[1]);
        if(matcher.find()){
            // Must ignore first line which is the signature of the statement
            this.line = Integer.parseInt(matcher.group(1)) ;
            // TODO recalculate the column number after replace parameters with the notation
            this.column = Integer.parseInt(matcher.group(2));
            this.exception = exception;
        }else
            throw new RuntimeException("Unexpected format of the message found." +
                    "\nLine and column should be printed.", exception);
    }
}
