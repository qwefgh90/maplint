package mybatis.parser.model;

import mybatis.parser.registry.ImmutableTypeHandlerRegistry;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.ResultSet;
// ParameterMapping.java
public class ParameterMapChild {

    private Config configuration;
    private String property;
    private ParameterMode mode;
    private String javaType = "java.lang.Object";
    private JdbcType jdbcType;
    private Integer numericScale;
    private String typeHandlerType;
    private TypeHandler typeHandler;
    private String resultMapId;
    private String jdbcTypeName;
    private String expression;
    private ResolvingType resolvingType;
    private NotationToken notation;
    private int index;

    private ParameterMapChild() {
    }

    public NotationToken getNotation() {
        return notation;
    }

    public Config getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public ParameterMode getMode() {
        return mode;
    }

    public String getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public Integer getNumericScale() {
        return numericScale;
    }

    public String getTypeHandlerType() {
        return typeHandlerType;
    }

    public TypeHandler getTypeHandler() {
        return typeHandler;
    }

    public String getResultMapId() {
        return resultMapId;
    }

    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    public String getExpression() {
        return expression;
    }

    public int getIndex() {
        return index;
    }

    public ResolvingType getResolvingType() {
        return resolvingType;
    }

    public static final class ParameterMapChildBuilder {
        private Config configuration;
        private String property;
        private ParameterMode mode = ParameterMode.IN;
        private String javaType = "java.lang.Object";
        private JdbcType jdbcType;
        private Integer numericScale;
        private String typeHandlerType;
        private TypeHandler typeHandler;
        private String resultMapId;
        private String jdbcTypeName;
        private String expression;
        private NotationToken notation;
        private ResolvingType resolvingType;
        private int index;

        private ParameterMapChildBuilder() {
        }

        public static ParameterMapChildBuilder aParameterMapChild() {
            return new ParameterMapChildBuilder();
        }

        public ParameterMapChildBuilder withResolvingType(ResolvingType resolvingType) {
            this.resolvingType = resolvingType;
            return this;
        }

        public ParameterMapChildBuilder withConfiguration(Config configuration) {
            this.configuration = configuration;
            return this;
        }

        public ParameterMapChildBuilder withIndex(int index) {
            this.index = index;
            return this;
        }

        public ParameterMapChildBuilder withNotation(NotationToken notation) {
            this.notation = notation;
            return this;
        }

        public ParameterMapChildBuilder withProperty(String property) {
            this.property = property;
            return this;
        }

        public ParameterMapChildBuilder withMode(ParameterMode mode) {
            this.mode = mode;
            return this;
        }

        public ParameterMapChildBuilder withJavaType(String javaType) {
            this.javaType = javaType;
            return this;
        }

        public ParameterMapChildBuilder withJdbcType(JdbcType jdbcType) {
            this.jdbcType = jdbcType;
            return this;
        }

        public ParameterMapChildBuilder withNumericScale(Integer numericScale) {
            this.numericScale = numericScale;
            return this;
        }

        public ParameterMapChildBuilder withTypeHandlerType(String typeHandlerType) {
            this.typeHandlerType = typeHandlerType;
            return this;
        }
        public ParameterMapChildBuilder withTypeHandlerType(TypeHandler typeHandler) {
            this.typeHandler = typeHandler;
            return this;
        }

        public ParameterMapChildBuilder withResultMapId(String resultMapId) {
            this.resultMapId = resultMapId;
            return this;
        }

        public ParameterMapChildBuilder withJdbcTypeName(String jdbcTypeName) {
            this.jdbcTypeName = jdbcTypeName;
            return this;
        }

        public ParameterMapChildBuilder withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public ParameterMapChild build() {
            resolveTypeHandler();
            assert(typeHandler != null); // TODO
            ParameterMapChild parameterMapChild = new ParameterMapChild();
            parameterMapChild.javaType = this.javaType;
            parameterMapChild.mode = this.mode;
            parameterMapChild.property = this.property;
            parameterMapChild.numericScale = this.numericScale;
            parameterMapChild.jdbcType = this.jdbcType;
            parameterMapChild.expression = this.expression;
            parameterMapChild.configuration = this.configuration;
            parameterMapChild.typeHandlerType = this.typeHandlerType;
            parameterMapChild.typeHandler = this.typeHandler;
            parameterMapChild.jdbcTypeName = this.jdbcTypeName;
            parameterMapChild.resultMapId = this.resultMapId;
            parameterMapChild.index = this.index;
            parameterMapChild.resolvingType = this.resolvingType;
            parameterMapChild.notation = this.notation;
            return parameterMapChild;
        }

        private void resolveTypeHandler() {
            if (this.typeHandler == null && this.javaType != null) {
                ImmutableTypeHandlerRegistry typeHandlerRegistry = ImmutableTypeHandlerRegistry.immutableTypeHandlerRegistry;
                this.typeHandler = typeHandlerRegistry.getTypeHandler(this.javaType, this.jdbcType);
            }
        }
    }

    @Override
    public String toString() {
        return "ParameterMapChild{" +
                "property='" + property + '\'' +
                ", javaType='" + javaType + '\'' +
                ", notation=" + notation +
                '}';
    }
}
