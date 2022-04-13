package mybatis.diagnostics.event;

public interface EventVisitor {
    void groupEnter(GroupEvent event, BaseEvent parent);
    boolean enter(BaseEvent event, BaseEvent parent);
    void groupExit(GroupEvent event, BaseEvent parent);
}
