package com.mad.practice.inclass11;

import java.util.List;

/**
 * Created by kiran on 4/9/2018.
 */

public class ImageUrl {
    String totalHits,total;
    List<URls> hits;

    @Override
    public String toString() {
        return "ImageUrl{" +
                "totalHits='" + totalHits + '\'' +
                ", total='" + total + '\'' +
                ", hits=" + hits +
                '}';
    }
}

class URls{
    String largeImageURL;

    @Override
    public String toString() {
        return "URls{" +
                "largeImageURL='" + largeImageURL + '\'' +
                '}';
    }
}