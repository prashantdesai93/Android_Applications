package com.mad.practice.inclass13;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 4/2/2018.
 */

public class User {


    public String id, email, fName, lName;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fName", fName);
        result.put("lName", lName);
        result.put("email", email);
        result.put("id", id);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", fname='" + fName + '\'' +
                ", lname='" + lName + '\'' +
                '}';
    }
}
