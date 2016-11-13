package com.example.newsapp;

import android.graphics.Bitmap;

/**
 * News Article model
 * Created by carlos on 11/12/16.
 */
class NewsArticle {
    private String title;
    private String section;
    private String image;
    private String url;

    NewsArticle(String title, String sectionName, String image, String url) {
        this.title = title;
        this.section = sectionName;
        this.image = image;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
