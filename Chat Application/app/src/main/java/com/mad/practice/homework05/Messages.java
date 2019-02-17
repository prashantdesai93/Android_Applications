package com.mad.practice.homework05;

import java.util.Comparator;

/**
 * Created by hp on 4/8/2018.
 */

public class Messages {
    String user_fname, user_lname, message, created_at;
    int user_id, id;

    public Messages() {
    }

    public Messages(String message) {
        this.message = message;
    }

    public static Comparator<Messages> MessageTimeComparator
            = new Comparator<Messages>() {

        public int compare(Messages m1, Messages m2) {

            String msg1 = m1.created_at;
            String msg2 = m2.created_at;

            //ascending order
            return msg1.compareTo(msg2);

            //descending order
            //return msg2.compareTo(msg1);
        }
    };

        @Override
    public String toString() {
        return "Messages{" +
                "user_fname='" + user_fname + '\'' +
                ", user_lname='" + user_lname + '\'' +
                ", message='" + message + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user_id=" + user_id +
                ", id=" + id +
                '}';
    }
}
