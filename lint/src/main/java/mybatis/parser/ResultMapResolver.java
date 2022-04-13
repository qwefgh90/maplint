package mybatis.parser;

import mybatis.parser.model.Discriminator;
import mybatis.parser.model.ResultMap;
import mybatis.parser.model.ResultMapChild;

import java.util.List;

public class ResultMapResolver {
    private final MapperParserHelper assistant;
    private final String id;
    private final String type;
    private final String extend;
    private final Discriminator discriminator;
    private final List<ResultMapChild> resultMappings;
    private final Boolean autoMapping;

    public ResultMapResolver(MapperParserHelper assistant, String id, String type, String extend, Discriminator discriminator, List<ResultMapChild> resultMappings, Boolean autoMapping) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.extend = extend;
        this.discriminator = discriminator;
        this.resultMappings = resultMappings;
        this.autoMapping = autoMapping;
    }

    public ResultMap resolve() {
        return assistant.addResultMap(this.id, this.type, this.extend, this.discriminator, this.resultMappings, this.autoMapping);
    }
}
