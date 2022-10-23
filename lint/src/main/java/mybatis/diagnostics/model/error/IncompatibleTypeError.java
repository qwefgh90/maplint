package mybatis.diagnostics.model.error;

import sql.analysis.util.SourcePosition;
import mybatis.diagnostics.model.context.Context;
import mybatis.diagnostics.model.error.base.Error;
import mybatis.parser.model.MapperStatement;

public class IncompatibleTypeError extends Error<IncompatibleTypeErrorSource> {
    private final String summary;
    private final String details;

    public IncompatibleTypeError(MapperStatement mapperStatement, IncompatibleTypeErrorSource source) {
        super(new Context<>(source, mapperStatement));
        var formatString = "%s (%s, %d:%d-%d:%d) and %s (%s, %d:%d-%d:%d) are not compatible.";
//        var pos = getContext().getSource().sourcePosition;
        var colPos = SourcePosition.getSourcePosition(source.columnNode);
        var valPos = SourcePosition.getSourcePosition(source.valueNode)
                ;
        details = String.format(formatString
                , source.columnNamed.getFqn()
                , source.columnJDBCType.getName()
                , colPos == null ? 0 : colPos.beginLine
                , colPos == null ? 0 : colPos.beginColumn
                , colPos == null ? 0 : colPos.endLine
                , colPos == null ? 0 : colPos.endColumn
                , source.child.getNotation().getToken()
                , source.child.getJavaType()
                , valPos == null ? 0 : valPos.beginLine
                , valPos == null ? 0 : valPos.beginColumn
                , valPos == null ? 0 : valPos.endLine
                , valPos == null ? 0 : valPos.endColumn);
        summary = details;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return summary;
    }
}
