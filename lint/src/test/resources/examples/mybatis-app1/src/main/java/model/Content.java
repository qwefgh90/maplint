package model;

import java.time.LocalDateTime;

public class Content {
    public Content() {
    }

    public Content(int id, Author author, int blogId, String content, LocalDateTime createTime) {
        this.id = id;
        this.author = author;
        this.blogId = blogId;
        this.content = content;
        this.createTime = createTime;
    }

    private int id;
    private Author author;
    private int blogId;
    private String content;
    private LocalDateTime createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", author=" + author +
                ", blogId=" + blogId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}

