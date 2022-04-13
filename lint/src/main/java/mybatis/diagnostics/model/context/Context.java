package mybatis.diagnostics.model.context;

import mybatis.parser.model.MapperStatement;

public class Context<T> {
    protected final T source;
    protected final MapperStatement mapperStatement;

    public Context(T source, MapperStatement mapperStatement) {
        this.source = source;
        this.mapperStatement = mapperStatement;
    }

    public T getSource() {
        return source;
    }

    public MapperStatement getMapperStatement() {
        return mapperStatement;
    }

    @Override
    public String toString() {
        return "Context{" +
                "source=" + source +
                ", mapperStatement=" + mapperStatement.getId() +
                '}';
    }
}
