package mybatis.diagnostics.model.context;

public interface ContextProvider<T> {
    Context<T> getContext();
}
