package model;

import java.util.Date;

public class NaverBlog {
    String naver_owner;
    String naver_name;
    Date naver_update_time;

    @Override
    public String toString() {
        return "NaverBlog{" +
                "naver_owner='" + naver_owner + '\'' +
                ", naver_name='" + naver_name + '\'' +
                ", naver_update_date=" + naver_update_time +
                '}';
    }
}
