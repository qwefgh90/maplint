package mybatis.parser.keygen;

import mybatis.executor.Executor;
import mybatis.parser.model.MapperStatement;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;

public class NoKeyGenerator  implements KeyGenerator {

    /**
     * A shared instance.
     *
     * @since 3.4.3
     */
    public static final NoKeyGenerator INSTANCE = new NoKeyGenerator();


    @Override
    public void processBefore(mybatis.executor.Executor executor, MapperStatement ms, Statement stmt, Object parameter) {
        //Do Nothing
    }

    @Override
    public void processAfter(Executor executor, MapperStatement ms, Statement stmt, Object parameter) {
        //Do Nothing
    }
}
