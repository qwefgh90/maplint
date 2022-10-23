package mybatis.diagnostics.model.error;

import mybatis.diagnostics.analysis.database.model.SourcePosition;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.util.validation.metadata.Named;

/**
 * @author qwefgh90
 */
public class ObjectNameNotFoundErrorSource {
    public final Named named;
    public final ASTNodeAccess astNodeAccess;
    public final SourcePosition sourcePosition;

    public ObjectNameNotFoundErrorSource(Named named, ASTNodeAccess astNodeAccess) {
        this.named = named;
        this.astNodeAccess = astNodeAccess;
        this.sourcePosition = SourcePosition.getSourcePosition(astNodeAccess);
    }
}
