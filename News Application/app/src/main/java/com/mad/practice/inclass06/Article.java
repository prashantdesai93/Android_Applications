package com.mad.practice.inclass06;

/**
 * Created by hp on 2/19/2018.
 */

public class Article {
    String title, description, urlToImg, publishedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImg() {
        return urlToImg;
    }

    public void setUrlToImg(String urlToImg) {
        this.urlToImg = urlToImg;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", urlToImg='" + urlToImg + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }

    public Article() {
    }
}
