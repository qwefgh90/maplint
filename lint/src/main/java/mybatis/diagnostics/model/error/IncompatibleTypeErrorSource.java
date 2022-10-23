package mybatis.diagnostics.model.error;

import sql.analysis.util.SourcePosition;
import mybatis.parser.model.ParameterMapChild;
import net.sf.jsqlparser.parser.ASTNodeAccess;
import net.sf.jsqlparser.util.validation.metadata.Named;

import java.sql.JDBCType;
import java.util.Optional;
import java.util.Set;

public class IncompatibleTypeErrorSource {
    public final JDBCType columnJDBCType;
    public final Set<JDBCType> valueJDBCTypeList;
    public final Named columnNamed;
    public final ASTNodeAccess columnNode;
    public final ASTNodeAccess valueNode;
    public final ParameterMapChild child;
    public final Optional<ASTNodeAccess> representationNode;
    public final Optional<SourcePosition> sourcePosition;

    public IncompatibleTypeErrorSource(JDBCType columnJDBCType, Set<JDBCType> valueJDBCTypeList, Named columnNamed, ASTNodeAccess columnNode, ASTNodeAccess valueNode, Optional<ASTNodeAccess> node, ParameterMapChild child) {
        this.columnJDBCType = columnJDBCType;
        this.valueJDBCTypeList = valueJDBCTypeList;
        this.columnNamed = columnNamed;
        this.columnNode = columnNode;
        this.valueNode = valueNode;
        this.representationNode = node;
        this.child = child;
        if(representationNode.isPresent())
            this.sourcePosition = Optional.of(SourcePosition.getSourcePosition(representationNode.get()));
        else
            this.sourcePosition = Optional.empty();
    }

    @Override
    public String toString() {
        return "IncompatibleTypeErrorSource{" +
                "columnNamed=" + columnNamed +
                ", valueNode=" + valueNode +
                ", sourcePosition=" + sourcePosition +
                '}';
    }
}
