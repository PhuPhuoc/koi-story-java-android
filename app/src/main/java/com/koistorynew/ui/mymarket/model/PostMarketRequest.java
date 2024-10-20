package com.koistorynew.ui.mymarket.model;

import java.util.List;

public class PostMarketRequest {
    private String color;
    private String created_at;
    private String description;
    private List<String> list_image;
    private String old;
    private String phone_number;
    private String post_type;
    private int price;
    private String product_name;
    private String product_type;
    private String seller_address;
    private String size;
    private String title;
    private String type;
    private String user_id;

    public PostMarketRequest(String color, List<String> list_image, String created_at, String description, String phone_number, String old, String post_type, int price, String product_name, String seller_address, String product_type, String size, String title, String user_id, String type) {
        this.color = color;
        this.list_image = list_image;
        this.created_at = created_at;
        this.description = description;
        this.phone_number = phone_number;
        this.old = old;
        this.post_type = post_type;
        this.price = price;
        this.product_name = product_name;
        this.seller_address = seller_address;
        this.product_type = product_type;
        this.size = size;
        this.title = title;
        this.user_id = user_id;
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getDescription() {
        return description;
    }

    public String getPost_type() {
        return post_type;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getSeller_address() {
        return seller_address;
    }

    public String getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getType() {
        return type;
    }

    public String getProduct_type() {
        return product_type;
    }

    public int getPrice() {
        return price;
    }

    public List<String> getList_image() {
        return list_image;
    }

    public String getOld() {
        return old;
    }

    public String getPhone_number() {
        return phone_number;
    }
}
