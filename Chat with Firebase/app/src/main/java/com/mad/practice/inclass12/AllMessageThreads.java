package com.mad.practice.inclass12;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hp on 4/7/2018.
 */

public class AllMessageThreads implements Serializable{

    public ArrayList<Thread> threads = new ArrayList<>();
//    String status, message;


    @Override
    public String toString() {
        return "AllMessageThreads{" +
                "threads=" + threads +
                '}';
    }
}
