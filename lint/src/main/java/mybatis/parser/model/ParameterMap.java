package mybatis.parser.model;

import org.apache.ibatis.mapping.ParameterMapping;

import java.util.Collections;
import java.util.List;

public class ParameterMap {
    private String id;
    private String type;
    private List<ParameterMapChild> parameterMappings;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<ParameterMapChild> getParameterMappings() {
        return parameterMappings;
    }

    private ParameterMap() {
    }

    public static final class ParameterMapBuilder {
        private String id;
        private String type;
        private List<ParameterMapChild> parameterMappings;

        private ParameterMapBuilder() {
        }

        public static ParameterMapBuilder aParameterMap() {
            return new ParameterMapBuilder();
        }

        public ParameterMapBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ParameterMapBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ParameterMapBuilder withParameterMappings(List<ParameterMapChild> parameterMappings) {
            this.parameterMappings = parameterMappings;
            return this;
        }

        public ParameterMap build() {
            ParameterMap parameterMap = new ParameterMap();
            parameterMap.type = this.type;
            parameterMap.parameterMappings = Collections.unmodifiableList(this.parameterMappings);
            parameterMap.id = this.id;
            return parameterMap;
        }
    }
}
