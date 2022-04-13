package mybatis.diagnostics.model.error;

import mybatis.diagnostics.model.context.Context;
import mybatis.diagnostics.model.error.base.Error;
import mybatis.parser.model.MapperStatement;

public class ObjectNameNotFoundError extends Error<ObjectNameNotFoundErrorSource> {
    private final String summary;
    private final String details;

    public ObjectNameNotFoundError(MapperStatement mapperStatement, ObjectNameNotFoundErrorSource source) {
        super(new Context<>(source, mapperStatement));
        var formatString = "The %s of %s does not exist at %d:%d-%d:%d.";
        var pos = getContext().getSource().sourcePosition;
        details = String.format(formatString, source.named.getNamedObject(), source.named.getFqn(), pos.beginLine, pos.beginColumn, pos.endLine, pos.endColumn);
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
