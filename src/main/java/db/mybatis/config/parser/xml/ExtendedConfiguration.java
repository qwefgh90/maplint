package db.mybatis.config.parser.xml;

import db.mybatis.config.parser.type.ExtendedTypeAliasRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;

public class ExtendedConfiguration extends Configuration {
    public final ExtendedTypeAliasRegistry extendedTypeAliasRegistry;
    public ExtendedConfiguration(Environment environment) {
        super(environment);
        this.extendedTypeAliasRegistry = new ExtendedTypeAliasRegistry(super.getTypeAliasRegistry());
    }

    public ExtendedConfiguration() {
        super();
        this.extendedTypeAliasRegistry = new ExtendedTypeAliasRegistry(super.getTypeAliasRegistry());
    }

    @Override
    public TypeAliasRegistry getTypeAliasRegistry() {
        return this.extendedTypeAliasRegistry;
    }
}
