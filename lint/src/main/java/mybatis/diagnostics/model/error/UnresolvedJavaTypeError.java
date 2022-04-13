package mybatis.diagnostics.model.error;

import mybatis.diagnostics.model.context.Context;
import mybatis.diagnostics.model.error.base.Error;
import mybatis.parser.model.MapperStatement;
import mybatis.parser.model.ParameterMapChild;

public class UnresolvedJavaTypeError extends Error<UnresolvedJavaTypeErrorSource> {

    public UnresolvedJavaTypeError(MapperStatement mapperStatement, ParameterMapChild source) {
        super(new Context<>(new UnresolvedJavaTypeErrorSource(mapperStatement.getBody(), source), mapperStatement));
    }

    @Override
    public String getDetails() {
        return String.format(
                "The type of %s was not resolved at %d:%d-%d:%d. parameterType is %s.",
                getContext().getSource().getParameterMapChild().getNotation().getToken(),
                getContext().getSource().getPosition().beginLine,
                getContext().getSource().getPosition().beginColumn,
                getContext().getSource().getPosition().endLine,
                getContext().getSource().getPosition().endColumn,
                getContext().getMapperStatement().getParameterType()
        );
    }

    @Override
    public String getSummary() {
        return String.format(
                "The type of %s was not resolved at %d:%d-%d:%d.",
                getContext().getSource().getParameterMapChild().getNotation().getToken(),
                getContext().getSource().getPosition().beginLine,
                getContext().getSource().getPosition().beginColumn,
                getContext().getSource().getPosition().endLine,
                getContext().getSource().getPosition().endColumn
        );
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
