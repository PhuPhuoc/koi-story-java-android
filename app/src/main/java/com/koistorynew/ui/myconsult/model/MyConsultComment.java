package com.koistorynew.ui.myconsult.model;

public class MyConsultComment {
    private String userName;
    private String commentDate;
    private String commentText;
    private String avatarUrl;


    public MyConsultComment(String userName, String commentDate, String commentText, String avatarUrl) {
        this.userName = userName;
        this.commentDate = commentDate;
        this.commentText = commentText;
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
