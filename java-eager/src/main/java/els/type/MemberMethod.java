package els.type;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.javacs.lsp.Location;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemberMethod extends Member {
    public final BaseType returnType;
    public final List<Parameter> paramters;
    public final Set<Parameter> paramterSet;

    public MemberMethod(String name, boolean deprecated, Location location, String containerName
            , BaseType returnType, List<Parameter> paramters, Symbol symbol) {
        super(name, deprecated, location, containerName, symbol);
        this.returnType = returnType;
        this.paramters = paramters;
        this.paramterSet = new HashSet<>(paramters);
    }

    public Map<String, Parameter> getParameterMap(){
        return this.paramters.stream().collect(Collectors.toMap(Parameter::getName, Function.identity()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MemberMethod that = (MemberMethod) o;
        return returnType.equals(that.returnType) && paramterSet.equals(((MemberMethod) o).paramterSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), returnType, paramterSet);
    }
}
