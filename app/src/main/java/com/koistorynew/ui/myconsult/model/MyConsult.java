package com.koistorynew.ui.myconsult.model;

public class MyConsult {
    private String id;
    private String user_id;
    private String user_name;
    private String user_avatar;
    private String post_type;
    private String title;
    private String content;
    private String image_id;
    private String file_path;
    private String created_at;

    public MyConsult(String id, String user_id, String user_name, String user_avatar, String post_type, String title, String content, String image_id, String file_path, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_avatar = user_avatar;
        this.post_type = post_type;
        this.title = title;
        this.content = content;
        this.image_id = image_id;
        this.file_path = file_path;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public String getPost_type() {
        return post_type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage_id() {
        return image_id;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getCreated_at() {
        return created_at;
    }
}
