package els.type;

import com.sun.tools.javac.code.Symbol;

import java.util.Objects;

public class Parameter extends BaseType {
    public final String name;
    public final BaseType parameterType;

    public Parameter(Symbol symbol, String name, BaseType parameterType) {
        super(symbol);
        this.name = name;
        this.parameterType = parameterType;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Parameter parameter = (Parameter) o;
        return name.equals(parameter.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
