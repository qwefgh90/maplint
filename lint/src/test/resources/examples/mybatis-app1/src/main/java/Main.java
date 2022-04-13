import model.Author;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        String resource = "config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(inputStream);
        var session = sqlSessionFactory.openSession();

        var key = session.insert("db.BlogMapper.insertAuthor"
                , new Author(0, "창원"));
        var authors = session.selectList("db.BlogMapper.selectAuthor");
    }
}
