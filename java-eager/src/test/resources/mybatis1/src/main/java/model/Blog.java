package model;

import java.time.LocalDateTime;

public class Blog{
    int id;
    String blogName;
    int category;
    String owner;
    LocalDateTime updateTime;

    public Blog(int id, String blogName, int category, String owner, LocalDateTime updateTime) {
        this.id = id;
        this.blogName = blogName;
        this.category = category;
        this.owner = owner;
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", name='" + blogName + '\'' +
                ", category=" + category +
                ", owner='" + owner + '\'' +
                ", update_time=" + updateTime +
                '}';
    }
}