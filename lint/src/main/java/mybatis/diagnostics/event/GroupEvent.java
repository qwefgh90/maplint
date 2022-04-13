package mybatis.diagnostics.event;

import mybatis.diagnostics.model.DiagnosticSource;
import mybatis.diagnostics.model.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupEvent extends BaseEvent<DiagnosticSource> {
    protected List<BaseEvent> children = new ArrayList<>();

    public GroupEvent(BaseEvent startEvent, Context<DiagnosticSource> context) {
        super(context, LocalDateTime.now());
        log(startEvent);
    }

    public GroupEvent(Context<DiagnosticSource> context) {
        super(context, LocalDateTime.now());
    }

    public GroupEvent group(BaseEvent startEvent, Context<DiagnosticSource> context) {
        var writer = new GroupEvent(startEvent, context);
        log(writer);
        return writer;
    }

    public GroupEvent group(Context<DiagnosticSource> context) {
        var writer = new GroupEvent(context);
        log(writer);
        return writer;
    }

    public void log(BaseEvent t) {
        children.add(t);
    }

    public Iterator<BaseEvent> iterator() {
        sort();
        return children.iterator();
    }

    protected void sort() {
        children.sort((e1, e2) ->
            e1.getTime().isAfter(e2.getTime()) ? 1 : -1
        );
    }

    @Override
    public boolean accept(EventVisitor visitor, BaseEvent parent) {
        visitor.groupEnter(this, parent);
        if(super.accept(visitor, parent)) {
            for (var child : children) {
                if(!child.accept(visitor, this))
                    break;
            }
        }
        visitor.groupExit(this, parent);
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getContext().getSource().getType().toString(), getContext().getMapperStatement() != null ? getContext().getMapperStatement().getId() : "");
    }
}
