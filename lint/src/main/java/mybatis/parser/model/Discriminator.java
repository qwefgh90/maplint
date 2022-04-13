package mybatis.parser.model;

import org.apache.ibatis.mapping.ResultMapping;

import java.util.Map;

public class Discriminator {

    private ResultMapChild resultMapChild;
    private Map<String, String> discriminatorMap;

    public ResultMapChild getResultMapChild() {
        return resultMapChild;
    }

    public Map<String, String> getDiscriminatorMap() {
        return discriminatorMap;
    }

    public Discriminator(ResultMapChild resultMapChild, Map<String, String> discriminatorMap) {
        this.resultMapChild = resultMapChild;
        this.discriminatorMap = discriminatorMap;
    }

    public static final class DiscriminatorBuilder {
        private ResultMapChild resultMapChild;
        private Map<String, String> discriminatorMap;

        private DiscriminatorBuilder() {
        }

        public static DiscriminatorBuilder aDiscriminator() {
            return new DiscriminatorBuilder();
        }

        public DiscriminatorBuilder withResultMapChild(ResultMapChild resultMapChild) {
            this.resultMapChild = resultMapChild;
            return this;
        }

        public DiscriminatorBuilder withDiscriminatorMap(Map<String, String> discriminatorMap) {
            this.discriminatorMap = discriminatorMap;
            return this;
        }

        public Discriminator build() {
            return new Discriminator(resultMapChild, discriminatorMap);
        }
    }
}
