package com.mad.practice.inclass03;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by kiran on 1/29/2018.
 */

public class Student implements Serializable{
    String name;
    String email;
    String dept;
    String mood;

    public Student(String name, String email, String dept, String mood) {
        this.name = name;
        this.email = email;
        this.dept = dept;
        this.mood = mood;
    }

    public Student() {
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dept='" + dept + '\'' +
                ", mood='" + mood + '\'' +
                '}';
    }
}
