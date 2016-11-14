package com.example.newsapp;

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

    String getTitle() {
        return title;
    }
    String getSection() {
        return section;
    }
    String getImage() {
        return image;
    }
    String getUrl() {
        return url;
    }
}
