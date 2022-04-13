package mybatis.diagnostics.analysis.structure.visitor;

import mybatis.diagnostics.analysis.base.AdditionalContextKey;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.ValidationException;
import net.sf.jsqlparser.util.validation.metadata.MetadataContext;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ASTNodeCollector implements ValidationCapability {
    protected Map<Named, List<ASTNodeAccess>> columnNodeMap = new HashMap<>();

    public Map<Named, List<ASTNodeAccess>> getColumnNodeMap() {
        return columnNodeMap;
    }

    @Override
    public String getName() {
        return ValidationCapability.super.getName();
    }

    @Override
    public void validate(ValidationContext context, Consumer<ValidationException> errorConsumer) {
        Named named = context.get(MetadataContext.named, Named.class);
        var node = context.get(AdditionalContextKey.SimpleNode, ASTNodeAccess.class);
        columnNodeMap.computeIfAbsent(named, (key) -> new ArrayList<>()).add(node);
    }

    @Override
    public ValidationException toError(String message) {
        return ValidationCapability.super.toError(message);
    }

    @Override
    public ValidationException toError(String message, Throwable th) {
        return ValidationCapability.super.toError(message, th);
    }
}
