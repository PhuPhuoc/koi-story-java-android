package com.koistorynew.ui.myconsult.model;

public class MyConsultComment {
    private String userName;
    private String commentText;
    private String avatarUrl; // New field for avatar URL

    public MyConsultComment(String userName, String commentText, String avatarUrl) {
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
