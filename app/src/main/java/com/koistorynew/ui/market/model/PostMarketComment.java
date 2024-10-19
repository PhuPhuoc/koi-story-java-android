package com.koistorynew.ui.market.model;

public class PostMarketComment {
    private String userName;
    private String commentText;
    private String avatarUrl; // New field for avatar URL

    public PostMarketComment(String userName, String commentText, String avatarUrl) {
        this.userName = userName;
        this.commentText = commentText;
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
