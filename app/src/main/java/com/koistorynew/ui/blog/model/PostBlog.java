package com.koistorynew.ui.blog.model;

public class PostBlog {
    String id;
    String authorName;
    String postBlogImageUrl;
    String title;

    public PostBlog(String id, String authorName, String postBlogImageUrl, String title) {
        this.id = id;
        this.authorName = authorName;
        this.postBlogImageUrl = postBlogImageUrl;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPostBlogImageUrl() {
        return postBlogImageUrl;
    }

    public void setPostBlogImageUrl(String postBlogImageUrl) {
        this.postBlogImageUrl = postBlogImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
