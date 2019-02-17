package com.mad.practice.inclass09;

import java.util.ArrayList;

/**
 * Created by kiran on 3/26/2018.
 */

public class Loc {

    ArrayList<Point> points = new ArrayList<>();
    String title;

    @Override
    public String toString() {
        return "Loc{" +
                "points=" + points +
                ", title='" + title + '\'' +
                '}';
    }

    public static class Point{
        double latitude,longitude;

        @Override
        public String toString() {
            return "Point{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }
}
