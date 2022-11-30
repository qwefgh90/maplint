import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class JDBCTest {
    class Article {

        String title;
        String content;
        String writer;
        Date updateTime;

        public Article(String title, String content, String writer, Date updateTime) {
            this.title = title;
            this.content = content;
            this.writer = writer;
            this.updateTime = updateTime;
        }
    }

    class ArticleQuery {
        String writerName;
        String title;

        public String getTitle() {
            return title;
        }

        public ArticleQuery(String writerName) {
            this.writerName = writerName;
        }

        public String getWriterName() {
            return writerName;
        }

        public ArticleQuery(String writerName, String title) {
            this.writerName = writerName;
            this.title = title;
        }
    }

    Article selectArticle() throws SQLException {
        var conn = DriverManager.getConnection(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        var param = new ArticleQuery("Changwon", "Java");
        String selectArticleQuery = "select title, content, writer, update_time " +
                "from Article " +
                "where title like '%" + param.getTitle() + "%' " +
                "and writerName = ? ";
        PreparedStatement selectArticleStatement = conn.prepareStatement(selectArticleQuery);
        conn.setAutoCommit(false);
        selectArticleStatement.setString(1, param.getWriterName());
        var rs = selectArticleStatement.executeQuery();
        rs.next();
        var title = rs.getString("title");
        var content = rs.getString("content");
        var writer = rs.getString("writer");
        var updateTime = rs.getDate("updateTime");
        return new Article(title, content, writer, updateTime);
    }
}