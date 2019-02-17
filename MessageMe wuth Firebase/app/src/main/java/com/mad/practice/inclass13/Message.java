package com.mad.practice.inclass13;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Message {
    public String message,date,senderName,senderId, id;
    public boolean isMessageRead;

    public Message() {
    }

    public Message(String message, String date, String senderName, String senderId, boolean isMessageRead, String id) {
        this.message = message;
        this.date = date;
        this.senderName = senderName;
        this.senderId = senderId;
        this.isMessageRead = isMessageRead;
        this.id = id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("date", date);
        result.put("senderName", senderName);
        result.put("senderId", senderId);
        result.put("isMessageRead", false);
        result.put("id", id);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderId='" + senderId + '\'' +
                ", id='" + id + '\'' +
                ", isMessageRead=" + isMessageRead +
                '}';
    }
}
