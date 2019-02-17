package com.mad.practice.homework05;

import java.util.ArrayList;

/**
 * Created by hp on 4/8/2018.
 */

public class AllMessages {

    ArrayList<Messages> messages = new ArrayList<>();
    String status, message;

    @Override
    public String toString() {
        return "AllMessages{" +
                "messages=" + messages +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public AllMessages() {
    }
}
