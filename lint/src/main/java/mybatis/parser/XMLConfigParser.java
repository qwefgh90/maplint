package mybatis.parser;

import mybatis.parser.model.DataSourceConfig;
import mybatis.parser.model.Environment;
import mybatis.parser.model.TransactionManager;
import mybatis.parser.model.Config;
import mybatis.project.MyBatisProjectService;
import mybatis.type.jtj.TypeResolver;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

import java.io.InputStream;
import java.util.Properties;

/**
 * This is a different XMLConfigBuilder using The Java Language Server instead of the class resolver.
 */
public class XMLConfigParser extends BaseParser {

    private boolean parsed;
    private final XPathParser parser;
    private String environment;
    private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();
    private final MyBatisProjectService myBatisProjectService;

    public XMLConfigParser(InputStream inputStream, MyBatisProjectService myBatisProjectService) {
        this(inputStream, null, null, myBatisProjectService);
    }

    public XMLConfigParser(InputStream inputStream, String environment, Properties props, MyBatisProjectService myBatisProjectService) {
        this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props, myBatisProjectService);
    }

    private XMLConfigParser(XPathParser parser, String environment, Properties props, MyBatisProjectService myBatisProjectService) {
        super(new Config());
        ErrorContext.instance().resource("SQL Mapper Configuration");
        this.configuration.setVariables(props);
        this.parsed = false;
        this.environment = environment;
        this.parser = parser;
        this.myBatisProjectService = myBatisProjectService;
        this.configuration.setMyBatisProjectService(myBatisProjectService);
    }

    public Config parse() {
        if (parsed) {
            throw new BuilderException("Each XMLConfigParser can only be used once.");
        }
        parsed = true;
        configuration.setTypeResolver(new TypeResolver(myBatisProjectService));
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            // issue #117 read properties first
            Properties settings = settingsAsProperties(root.evalNode("settings"));
//            loadCustomVfs(settings);
//            loadCustomLogImpl(settings);
//            typeAliasesElement(root.evalNode("typeAliases"));
//            pluginElement(root.evalNode("plugins"));
//            objectFactoryElement(root.evalNode("objectFactory"));
//            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
//            reflectorFactoryElement(root.evalNode("reflectorFactory"));
            settingsElement(settings);
            // read it after objectFactory and objectWrapperFactory issue #631

            databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            propertiesElement(root.evalNode("properties"));
            environmentsElement(root.evalNode("environments"));
//            typeHandlerElement(root.evalNode("typeHandlers"));
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    private Properties settingsAsProperties(XNode context) {
        if (context == null) {
            return new Properties();
        }
        Properties props = context.getChildrenAsProperties();
        // Check that all settings are known to the configuration class
        MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
            }
        }
        return props;
    }

    private void propertiesElement(XNode context) throws Exception {
        if (context != null) {
            Properties defaults = context.getChildrenAsProperties();
            String resource = context.getStringAttribute("resource");
            String url = context.getStringAttribute("url");
            if (resource != null && url != null) {
                throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
            }
            if (resource != null) {
                defaults.putAll(Resources.getResourceAsProperties(resource));
            } else if (url != null) {
                defaults.putAll(Resources.getUrlAsProperties(url));
            }
            Properties vars = configuration.getVariables();
            if (vars != null) {
                defaults.putAll(vars);
            }
            parser.setVariables(defaults);
            configuration.setVariables(defaults);
        }
    }

    private void settingsElement(Properties props) {
        configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
        configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
        configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
//        configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
        configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
        configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), false));
        configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
        configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
        configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
        configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
        configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
        configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
        configuration.setDefaultResultSetType(resolveResultSetType(props.getProperty("defaultResultSetType")));
        configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
        configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
        configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
        configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
        configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
//        configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
//        configuration.setDefaultEnumTypeHandler(resolveClass(props.getProperty("defaultEnumTypeHandler")));
//        configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
        configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
        configuration.setReturnInstanceForEmptyRow(booleanValueOf(props.getProperty("returnInstanceForEmptyRow"), false));
        configuration.setLogPrefix(props.getProperty("logPrefix"));
//        configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
        configuration.setShrinkWhitespacesInSql(booleanValueOf(props.getProperty("shrinkWhitespacesInSql"), false));
//        configuration.setDefaultSqlProviderType(resolveClass(props.getProperty("defaultSqlProviderType")));
        configuration.setNullableOnForEach(booleanValueOf(props.getProperty("nullableOnForEach"), false));
    }

    private void environmentsElement(XNode context) throws Exception {
        if (context != null) {
            if (environment == null) {
                environment = context.getStringAttribute("default");
            }
            for (XNode child : context.getChildren()) {
                String id = child.getStringAttribute("id");
                if (isSpecifiedEnvironment(id)) {
                    TransactionManager txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                    DataSourceConfig dsFactory = dataSourceElement(child.evalNode("dataSource"));
//                    DataSource dataSource = dsFactory.getDataSource();
                    Environment environment = new Environment(dsFactory, txFactory, id);
                    configuration.setEnvironment(environment);
                    break;
                }
            }
        }
    }

    private void databaseIdProviderElement(XNode context) throws Exception {
        DatabaseIdProvider databaseIdProvider = null;
        if (context != null) {
            String type = context.getStringAttribute("type");
            // awful patch to keep backward compatibility
            if ("VENDOR".equals(type)) {
                type = "DB_VENDOR";
            }
            Properties properties = context.getChildrenAsProperties();
            databaseIdProvider = new VendorDatabaseIdProvider(); //resolveClass(type).getDeclaredConstructor().newInstance();
            databaseIdProvider.setProperties(properties);
        }
        Environment environment = configuration.getEnvironment();
        if (environment != null && databaseIdProvider != null) {
            String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSourceConfig().getDataSource());
            configuration.setDatabaseId(databaseId);
        }
    }

    private TransactionManager transactionManagerElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            //TODO: Only two types of the transaction factory are enabled unlike MyBatis.
            TransactionFactory factory = (TransactionFactory) immutableTypeAliasRegistry.resolveAlias(type).getDeclaredConstructor().newInstance();
            factory.setProperties(props);
            return new TransactionManager(props, type, factory);
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }

    private DataSourceConfig dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            DataSourceFactory factory = (DataSourceFactory) immutableTypeAliasRegistry.resolveAlias(type).getDeclaredConstructor().newInstance();
            factory.setProperties(props);
            return new DataSourceConfig(props, type, factory);
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }

//    private void typeHandlerElement(XNode parent) {
//        if (parent != null) {
//            for (XNode child : parent.getChildren()) {
//                if ("package".equals(child.getName())) {
//                    String typeHandlerPackage = child.getStringAttribute("name");
//                    typeHandlerRegistry.register(typeHandlerPackage);
//                } else {
//                    String javaTypeName = child.getStringAttribute("javaType");
//                    String jdbcTypeName = child.getStringAttribute("jdbcType");
//                    String handlerTypeName = child.getStringAttribute("handler");
//                    Class<?> javaTypeClass = resolveClass(javaTypeName);
//                    JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
//                    Class<?> typeHandlerClass = resolveClass(handlerTypeName);
//                    if (javaTypeClass != null) {
//                        if (jdbcType == null) {
//                            typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
//                        } else {
//                            typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
//                        }
//                    } else {
//                        typeHandlerRegistry.register(typeHandlerClass);
//                    }
//                }
//            }
//        }
//    }

    private void mapperElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
//                if ("package".equals(child.getName())) {
//                    String mapperPackage = child.getStringAttribute("name");
//                    configuration.addMappers(mapperPackage);
//                } else {
                String resource = child.getStringAttribute("resource");
                String url = child.getStringAttribute("url");
                String mapperClass = child.getStringAttribute("class");
                if (resource != null && url == null && mapperClass == null) {
                    ErrorContext.instance().resource(resource);
                    try (InputStream inputStream = this.myBatisProjectService.getResourceSystem().getResourceAsStream(resource)) {
                        XMLMapperParser mapperParser = new XMLMapperParser(inputStream, configuration, resource, configuration.getSqlFragments());
                        mapperParser.parse();
                    }
                } else if (resource == null && url != null && mapperClass == null) {
                    ErrorContext.instance().resource(url);
                    try (InputStream inputStream = Resources.getUrlAsStream(url)) {
                        XMLMapperParser mapperParser = new XMLMapperParser(inputStream, configuration, url, configuration.getSqlFragments());
                        mapperParser.parse();
                    }
                }
            }
        }
    }

    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new BuilderException("No environment specified.");
        }
        if (id == null) {
            throw new BuilderException("Environment requires an id attribute.");
        }
        return environment.equals(id);
    }

}
