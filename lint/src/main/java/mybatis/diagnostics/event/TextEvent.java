package mybatis.diagnostics.event;

import mybatis.diagnostics.model.DiagnosticSource;
import mybatis.diagnostics.model.context.Context;

import java.time.LocalDateTime;

public class TextEvent extends BaseEvent<DiagnosticSource> {
    protected final String text;

    public TextEvent(Context<DiagnosticSource> context, LocalDateTime time, String text) {
        super(context, time);
        this.text = text;
    }

    public TextEvent(Context<DiagnosticSource> context, String text) {
        super(context, LocalDateTime.now());
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
