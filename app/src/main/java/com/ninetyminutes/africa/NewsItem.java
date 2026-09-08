package com.ninetyminutes.africa;

public class NewsItem {

    private final String id;
    private final String title;
    private final String excerpt;
    private final String content;
    private final String category;
    private final String author;
    private final String imageUrl;
    private final String createdAt;
    private final int views;

    public NewsItem(
            String id,
            String title,
            String excerpt,
            String content,
            String category,
            String author,
            String imageUrl,
            String createdAt,
            int views
    ) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.category = category;
        this.author = author;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getViews() {
        return views;
    }
}
