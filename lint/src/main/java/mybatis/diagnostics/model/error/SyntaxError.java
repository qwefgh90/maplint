package mybatis.diagnostics.model.error;

import mybatis.diagnostics.model.error.base.Error;
import mybatis.diagnostics.model.context.Context;
import mybatis.parser.model.MapperStatement;
import net.sf.jsqlparser.JSQLParserException;

public class SyntaxError extends Error<SyntaxErrorSource> {
    private final String summary;
    private final String details;

    public SyntaxError(MapperStatement mapperStatement, JSQLParserException exception) {
        super(new Context<>(new SyntaxErrorSource(exception), mapperStatement));
        var source = getContext().getSource();
        details = exception.getMessage();
        summary = String.format("%s at line %d, column %d.",
                exception.getMessage().split("\n")[0],
                source.line,
                source.column
        );
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
