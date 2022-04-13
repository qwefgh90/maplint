package els.type;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.javacs.lsp.Location;

import java.util.Objects;

public abstract class Member extends BaseType {
    public final String name;
    public final boolean deprecated;
    public final Location location;
    public final String containerName;

    protected Member(String name, boolean deprecated, Location location, String containerName, Symbol symbol) {
        super(symbol);
        this.name = name;
        this.deprecated = deprecated;
        this.location = location;
        this.containerName = containerName;
//        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member that = (Member) o;
        return name.equals(that.name) && Objects.equals(containerName, that.containerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, containerName);
    }
}
