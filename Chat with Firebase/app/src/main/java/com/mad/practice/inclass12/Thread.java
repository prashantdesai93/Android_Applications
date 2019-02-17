package com.mad.practice.inclass12;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Thread {

    public String id, userId,title, createdAt;


    public Thread() {
    }

    public Thread(String title,String id,String userId) {
        this.title = title;
        this.id=id;
        this.userId=userId;
        this.createdAt = new Date().toString();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("id", id);
        result.put("userId", userId);
        result.put("createdAt", createdAt);
        return result;
    }

    @Override
    public String toString() {
        return "Thread{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
