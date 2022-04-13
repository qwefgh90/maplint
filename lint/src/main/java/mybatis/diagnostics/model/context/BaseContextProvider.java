package mybatis.diagnostics.model.context;

public class BaseContextProvider<T> implements ContextProvider<T> {
    private final Context<T> context;

    public BaseContextProvider(Context<T> context) {
        this.context = context;
    }

    @Override
    public Context<T> getContext() {
        return context;
    }

    @Override
    public String toString() {
        return context.toString();
    }
}
