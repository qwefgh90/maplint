package mybatis.parser.registry;

import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public class ImmutableLanguageRegistry {
    LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
    public ImmutableLanguageRegistry() {
        super();
        languageRegistry.setDefaultDriverClass(org.apache.ibatis.scripting.xmltags.XMLLanguageDriver.class);
        languageRegistry.register(RawLanguageDriver.class);
    }

    public LanguageDriver getDriver(Class<? extends LanguageDriver> cls) {
        return languageRegistry.getDriver(cls);
    }

    public LanguageDriver getDefaultDriver() {
        return languageRegistry.getDefaultDriver();
    }

    public Class<? extends LanguageDriver> getDefaultDriverClass() {
        return languageRegistry.getDefaultDriverClass();
    }
}
