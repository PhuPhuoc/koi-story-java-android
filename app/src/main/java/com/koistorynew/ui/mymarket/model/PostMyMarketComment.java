package com.koistorynew.ui.mymarket.model;

public class PostMyMarketComment {
    private String userName;
    private String commentDate;
    private String commentText;
    private String avatarUrl; // New field for avatar URL

    public PostMyMarketComment(String userName, String commentDate, String commentText, String avatarUrl) {
        this.userName = userName;
        this.commentDate =commentDate;
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

    public String getCommentDate() {
        return commentDate;
    }
}
