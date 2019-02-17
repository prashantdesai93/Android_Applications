package com.mad.practice.homework05;

/**
 * Created by hp on 4/7/2018.
 */

public class Thread {

    String user_fname, user_lname, title, created_at;
    int user_id, id;

    public Thread() {
    }

    public Thread(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "threads{" +
                "user_fname='" + user_fname + '\'' +
                ", user_lname='" + user_lname + '\'' +
                ", title='" + title + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id=" + user_id +
                ", id=" + id +
                '}';
    }

}
