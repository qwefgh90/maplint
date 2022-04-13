package mybatis.diagnostics.analysis.structure.visitor;

import net.sf.jsqlparser.parser.feature.FeatureConfiguration;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.ValidationException;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author qwefgh90
 */
public interface DefaultContextProvider {

    default ValidationContext createValidationContext(ValidationCapability ...capabilities) {
        ValidationContext context = new ValidationContext();
        context.setCapabilities(capabilities.length == 0 ?
                Arrays.asList(DummyCapability.getInstance()) : Arrays.asList(capabilities));
        context.setConfiguration(new FeatureConfiguration());
        return context;
    }

    class DummyCapability implements ValidationCapability {

        @Override
        public void validate(ValidationContext context, Consumer<ValidationException> errorConsumer) {

        }

        private static DummyCapability instance;
        public static DummyCapability getInstance(){
            if(instance == null)
                instance = new DummyCapability();
            return instance;
        }
    }
}

