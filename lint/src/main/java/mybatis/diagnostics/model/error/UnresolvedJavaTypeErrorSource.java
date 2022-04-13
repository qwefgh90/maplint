package mybatis.diagnostics.model.error;

import mybatis.diagnostics.analysis.jdbc.model.SourcePosition;
import mybatis.parser.model.ParameterMapChild;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UnresolvedJavaTypeErrorSource{
    private final ParameterMapChild parameterMapChild;
    public final SourcePosition position;

    public UnresolvedJavaTypeErrorSource(String body, ParameterMapChild source) {
        this.parameterMapChild = source;
        var matcher = Pattern.compile("\n").matcher(body);
        var lineList = matcher.results()
                .map(result -> result.end())
                .filter(index -> index < body.length())
                .collect(Collectors.toList());
        lineList.add(0, 0);
        for(int i=0; i<lineList.size(); i++){
            var lineStartPosition = lineList.get(i);
            var lineEndPosition = i+1 < lineList.size() ? lineList.get(i+1) : body.length();
            if((lineStartPosition <= source.getNotation().getStart())
                    && (source.getNotation().getStart() < lineEndPosition)){
                var beginLine = i+1;
                var beginColumn = source.getNotation().getStart() - lineStartPosition + 1;
                //TODO: It assumes that first and last position on the same line.
                position = new SourcePosition(beginLine, beginColumn
                        , beginLine
                        , beginColumn+source.getNotation().getToken().length()-1);
                return;
            }
        }
        throw new RuntimeException("There isn't line and column found.");

    }

    public SourcePosition getPosition() {
        return position;
    }

    public ParameterMapChild getParameterMapChild() {
        return parameterMapChild;
    }

    @Override
    public String toString() {
        return "UnresolvedJavaTypeErrorSource{" +
                ", position=" + position +
                ", parameterMapChild=" + parameterMapChild +
                '}';
    }
}