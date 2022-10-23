package sql.analysis.util;

import net.sf.jsqlparser.parser.ASTNodeAccess;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author qwefgh90
 */
public class SourcePosition {
    public final int beginLine;
    public final int beginColumn;
    //SourcePosition contain a character at endLine and endColumn
    public final int endLine;
    public final int endColumn;

    public SourcePosition(int beginLine, int beginColumn, int endLine, int endColumn) {
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public static SourcePosition getSourcePosition(ASTNodeAccess access) {
        if (access.getASTNode() == null)
            return null;
        var first = access.getASTNode().jjtGetFirstToken();
        var last = access.getASTNode().jjtGetLastToken();
        return new SourcePosition(first.beginLine, first.beginColumn, last.endLine, last.endColumn);
    }

    public static SourcePosition getSourcePosition(String mappedStatement, int startIndex, String notation){
        var matcher = Pattern.compile("\n").matcher(mappedStatement);
        var lineList = matcher.results()
                .map(result -> result.end())
                .filter(index -> index < mappedStatement.length())
                .collect(Collectors.toList());
        lineList.add(0, 0);
        for(int i=0; i<lineList.size(); i++){
            var lineStartPosition = lineList.get(i);
            var lineEndPosition = i+1 < lineList.size() ? lineList.get(i+1) : mappedStatement.length();
            if((lineStartPosition <= startIndex)
                    && (startIndex < lineEndPosition)){
                var beginLine = i+1;
                var beginColumn = startIndex - lineStartPosition + 1;
                //TODO: It assumes that first and last position on the same line.
                var position = new SourcePosition(beginLine, beginColumn
                        , beginLine
                        , beginColumn + notation.length()-1);
                return position;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%d:%d-%d:%d", beginLine, beginColumn, endLine, endColumn);
    }
}
