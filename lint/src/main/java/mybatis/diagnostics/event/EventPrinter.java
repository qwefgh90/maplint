package mybatis.diagnostics.event;

import java.io.PrintStream;
import java.util.Stack;

public class EventPrinter {
    public static void print(GroupEvent group, PrintStream writer){
        final EventVisitor visitor = new EventVisitor() {
            Stack<Integer> oldOrderStack = new Stack<Integer>();
            int level = 0;
            int order = 1;
            @Override
            public void groupEnter(GroupEvent event, BaseEvent parent) {
                level++;
                oldOrderStack.push(order);
                order = 1;
            }

            @Override
            public boolean enter(BaseEvent event, BaseEvent parent) {
                if(event instanceof GroupEvent) {
                    writer.println(" ".repeat(level) + "ㄴ" + event.toString());
                }else {
                    writer.println(" ".repeat(level) + " " + order + ". " + event.toString());
                    order++;
                }
                return true;
            }

            @Override
            public void groupExit(GroupEvent event, BaseEvent parent) {
                order = oldOrderStack.pop();
                level--;
            }
        };
        group.accept(visitor, null);
    }
}
