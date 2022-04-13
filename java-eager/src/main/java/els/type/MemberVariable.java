package els.type;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.javacs.lsp.Location;

import java.util.Objects;

public class MemberVariable extends Member {

    public final BaseType variableType;

    public MemberVariable(String name
            , BaseType variableType
            , boolean deprecated
            , Location location
            , String containerName, Symbol symbol) {
        super(name, deprecated, location, containerName, symbol);
        this.variableType = variableType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MemberVariable that = (MemberVariable) o;
        return Objects.equals(variableType, that.variableType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), variableType);
    }
}
