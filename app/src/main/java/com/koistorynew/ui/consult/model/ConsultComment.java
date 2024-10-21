package com.koistorynew.ui.consult.model;

public class ConsultComment {
    private String userName;
    private String commentDate;
    private String commentText;
    private String avatarUrl;


    public ConsultComment(String userName, String commentDate, String commentText, String avatarUrl) {
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
