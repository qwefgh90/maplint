package model;

import java.util.List;

public class Author extends model.Blog implements java.util.List {
    String name;
    Content mainContent;
    List<Content> contentList;

    public Author(String name) {
        this.name = name;
    }

    public Author(String name, Content mainContent, List<Content> contentList) {
        this.name = name;
        this.mainContent = mainContent;
        this.contentList = contentList;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", mainContent=" + mainContent +
                ", contentList=" + contentList +
                '}';
    }
}
