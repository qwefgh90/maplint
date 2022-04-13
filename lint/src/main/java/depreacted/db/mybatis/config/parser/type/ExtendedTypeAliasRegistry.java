package depreacted.db.mybatis.config.parser.type;

import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeException;

import java.util.Map;

/**
 * ExtendedTypeAliasRegistry is a decorator class of TypeAliasRegistry.
 */
public class ExtendedTypeAliasRegistry extends TypeAliasRegistry {
    private TypeAliasRegistry typeAliasRegistry;
    public ExtendedTypeAliasRegistry(TypeAliasRegistry typeAliasRegistry) {
        super();
        this.typeAliasRegistry = typeAliasRegistry;
    }

    /**
     * return null instead of throwing an exception.
     * suppress ClassNotFoundException
     * @param string
     * @param <T>
     * @return
     */
    @Override
    public <T> Class<T> resolveAlias(String string) {
        try {
            return typeAliasRegistry.resolveAlias(string);
        }catch(TypeException ex){
            return null;
        }
    }

    @Override
    public Map<String, Class<?>> getTypeAliases() {
        return typeAliasRegistry.getTypeAliases();
    }
}
