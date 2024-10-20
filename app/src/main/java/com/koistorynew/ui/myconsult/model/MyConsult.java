package com.koistorynew.ui.myconsult.model;

public class MyConsult {
    private String id;
    private String userName;
    private String image;
    private String title;
    private String question;
    private String avatar;

    public MyConsult(String id, String image, String userName, String title, String question, String avatar) {
        this.id = id;
        this.image = image;
        this.userName = userName;
        this.title = title;
        this.question = question;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public String getAvatar() {
        return avatar;
    }
}
