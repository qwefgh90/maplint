package mybatis.parser.keygen;

import mybatis.executor.Executor;
import mybatis.parser.model.MapperStatement;

import java.sql.Statement;

public interface KeyGenerator {

    void processBefore(Executor executor, MapperStatement ms, Statement stmt, Object parameter);

    void processAfter(mybatis.executor.Executor executor, MapperStatement ms, Statement stmt, Object parameter);

}
