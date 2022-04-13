package mybatis.parser.model;

import java.util.List;
import java.util.Set;

public class ResultMap {
    private Config configuration;

    private String id;
    private String type;
    private List<ResultMapChild> resultMappings;
    private List<ResultMapChild> idResultMappings;
    private List<ResultMapChild> constructorResultMappings;
    private List<ResultMapChild> propertyResultMappings;
    private Set<String> mappedColumns;
    private Set<String> mappedProperties;
    private Discriminator discriminator;
    private boolean hasNestedResultMaps;
    private boolean hasNestedQueries;
    private Boolean autoMapping;

    private ResultMap() {
    }

    public Config getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<ResultMapChild> getResultMappings() {
        return resultMappings;
    }

    public List<ResultMapChild> getIdResultMappings() {
        return idResultMappings;
    }

    public List<ResultMapChild> getConstructorResultMappings() {
        return constructorResultMappings;
    }

    public List<ResultMapChild> getPropertyResultMappings() {
        return propertyResultMappings;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

    public Set<String> getMappedProperties() {
        return mappedProperties;
    }

    public Discriminator getDiscriminator() {
        return discriminator;
    }

    public boolean hasNestedResultMaps() {
        return hasNestedResultMaps;
    }

    public boolean hasNestedQueries() {
        return hasNestedQueries;
    }

    public Boolean getAutoMapping() {
        return autoMapping;
    }
    public void forceNestedResultMaps() {
        hasNestedResultMaps = true;
    }

    public static final class ResultMapBuilder {
        private Config configuration;
        private String id;
        private String type;
        private List<ResultMapChild> resultMappings;
        private List<ResultMapChild> idResultMappings;
        private List<ResultMapChild> constructorResultMappings;
        private List<ResultMapChild> propertyResultMappings;
        private Set<String> mappedColumns;
        private Set<String> mappedProperties;
        private Discriminator discriminator;
        private boolean hasNestedResultMaps;
        private boolean hasNestedQueries;
        private Boolean autoMapping;

        private ResultMapBuilder() {
        }

        public static ResultMapBuilder aResultMap() {
            return new ResultMapBuilder();
        }

        public ResultMapBuilder withConfiguration(Config configuration) {
            this.configuration = configuration;
            return this;
        }

        public ResultMapBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ResultMapBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ResultMapBuilder withResultMappings(List<ResultMapChild> resultMappings) {
            this.resultMappings = resultMappings;
            return this;
        }

        public ResultMapBuilder withIdResultMappings(List<ResultMapChild> idResultMappings) {
            this.idResultMappings = idResultMappings;
            return this;
        }

        public ResultMapBuilder withConstructorResultMappings(List<ResultMapChild> constructorResultMappings) {
            this.constructorResultMappings = constructorResultMappings;
            return this;
        }

        public ResultMapBuilder withPropertyResultMappings(List<ResultMapChild> propertyResultMappings) {
            this.propertyResultMappings = propertyResultMappings;
            return this;
        }

        public ResultMapBuilder withMappedColumns(Set<String> mappedColumns) {
            this.mappedColumns = mappedColumns;
            return this;
        }

        public ResultMapBuilder withMappedProperties(Set<String> mappedProperties) {
            this.mappedProperties = mappedProperties;
            return this;
        }

        public ResultMapBuilder withDiscriminator(Discriminator discriminator) {
            this.discriminator = discriminator;
            return this;
        }

        public ResultMapBuilder withHasNestedResultMaps(boolean hasNestedResultMaps) {
            this.hasNestedResultMaps = hasNestedResultMaps;
            return this;
        }

        public ResultMapBuilder withHasNestedQueries(boolean hasNestedQueries) {
            this.hasNestedQueries = hasNestedQueries;
            return this;
        }

        public ResultMapBuilder withAutoMapping(Boolean autoMapping) {
            this.autoMapping = autoMapping;
            return this;
        }

        public ResultMap build() {
            ResultMap resultMap = new ResultMap();
            resultMap.configuration = this.configuration;
            resultMap.constructorResultMappings = this.constructorResultMappings;
            resultMap.discriminator = this.discriminator;
            resultMap.resultMappings = this.resultMappings;
            resultMap.mappedColumns = this.mappedColumns;
            resultMap.idResultMappings = this.idResultMappings;
            resultMap.mappedProperties = this.mappedProperties;
            resultMap.autoMapping = this.autoMapping;
            resultMap.hasNestedResultMaps = this.hasNestedResultMaps;
            resultMap.propertyResultMappings = this.propertyResultMappings;
            resultMap.id = this.id;
            resultMap.type = this.type;
            resultMap.hasNestedQueries = this.hasNestedQueries;
            return resultMap;
        }
    }
}
