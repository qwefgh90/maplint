package els.type;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import javax.lang.model.type.TypeKind;
import java.util.Objects;

public class BaseType {
    public final Type originalType;
    public final Symbol originalSymbol;
    public final String packageName; //QualifiedName
    public final String owner; //QualifiedName
    public final String simpleName;
    public final String fullName; //QualifiedName org.springframework.boot.Banner.Mode
    public final String flatName; //QualifiedName org.springframework.boot.Banner$Mode
    public final String declaredName;
    public final TypeKind kind;

    /**
     * Symbol Constructor only is used on the subclass
     * @param originalClassSymbol
     */
    protected BaseType(Symbol originalClassSymbol) {
        this(originalClassSymbol.type, originalClassSymbol);
    }

    public BaseType(Type originalClassType) {
        this(originalClassType, originalClassType.tsym);
    }

    private BaseType(Type originalClassType, Symbol originalClassSymbol) {
        this.originalType = originalClassType;
        this.kind = originalClassType.getKind();
        this.originalSymbol = originalClassSymbol;
        this.declaredName = originalType.toString();
        this.fullName = originalSymbol.getQualifiedName().toString();
        this.flatName = originalSymbol.flatName().toString();
        this.packageName = originalSymbol.packge().fullname.toString();
        this.owner = originalSymbol.owner.getQualifiedName().toString();
        this.simpleName = originalSymbol.getSimpleName().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseType baseType = (BaseType) o;
        return originalType.equals(baseType.originalType) && packageName.equals(baseType.packageName) && simpleName.equals(baseType.simpleName) && fullName.equals(baseType.fullName) && kind == baseType.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalType, packageName, simpleName, fullName, kind);
    }
}
