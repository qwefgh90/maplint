package model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Blog{
    private int id;
    private Author author;
    private String blogName;
    private String description;
    private LocalDateTime createTime;
    private List<Content> contents;

    public Blog() {
    }

    public Blog(int id, Author author, String blogName, String description, LocalDateTime createTime, List<Content> contents) {
        this.id = id;
        this.author = author;
        this.blogName = blogName;
        this.description = description;
        this.createTime = createTime;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", author=" + author +
                ", blogName='" + blogName + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", contents=" + contents +
                '}';
    }

    public int secretlyGetId(){
        return id;
    }

    public List<Content> secretlyGetContents() {
        return contents;
    }
}