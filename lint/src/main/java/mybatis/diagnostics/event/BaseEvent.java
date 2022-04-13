package mybatis.diagnostics.event;

import mybatis.diagnostics.event.EventVisitor;
import mybatis.diagnostics.model.context.BaseContextProvider;
import mybatis.diagnostics.model.context.Context;

import java.time.LocalDateTime;

abstract public class BaseEvent<T> extends BaseContextProvider<T> {
    protected final LocalDateTime time;

    public BaseEvent(Context<T> context, LocalDateTime time) {
        super(context);
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public boolean accept(EventVisitor visitor, BaseEvent parent){
        return visitor.enter(this, parent);
    }
}
