package com.mad.practice.homework05;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hp on 4/7/2018.
 */

public class AllMessageThreads implements Serializable{

    ArrayList<Thread> threads = new ArrayList<>();
    String status, message;

    @Override
    public String toString() {
        return "AllMessageThreads{" +
                "threads=" + threads +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
/*public static class Thread{
        String user_fname, user_lname, title, created_at;
        int user_id, id;

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
    }*/

}
