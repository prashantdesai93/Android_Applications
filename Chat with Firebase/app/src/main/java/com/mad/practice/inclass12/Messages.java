package com.mad.practice.inclass12;

import com.google.firebase.database.Exclude;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 4/8/2018.
 */

public class Messages {
    /*String user_fname, user_lname, message, created_at;
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
    }*/

    public String user_name, id, userId, message, created_at;


    public Messages() {
    }

    public Messages(String message, String user_name, String id,String userId, String created_at) {
        this.message = message;
        this.id=id;
        this.userId=userId;
        this.user_name = user_name;
        this.created_at = created_at;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("id", id);
        result.put("userId", userId);
        result.put("created_at", created_at);
        result.put("user_name", user_name);
        return result;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "user_fname='" + user_name + '\'' +
                ", id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", createdAt='" + created_at + '\'' +
                '}';
    }
}
