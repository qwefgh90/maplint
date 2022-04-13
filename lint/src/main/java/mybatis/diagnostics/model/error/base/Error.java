package mybatis.diagnostics.model.error.base;

import mybatis.diagnostics.model.context.BaseContextProvider;
import mybatis.diagnostics.model.context.Context;

public abstract class Error<T> extends BaseContextProvider<T> {
    public Error(Context<T> context) {
        super(context);
    }

    public abstract String getDetails();
    public abstract String getSummary();
}
