package com.koistorynew.ui.myconsult.model;

import java.util.List;

public class AddConsult {
    private String id;
    private String userId;
    private String productName;
    private String description;
    private List<String> listImage;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getListImage() {
        return listImage;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setListImage(List<String> listImage) {
        this.listImage = listImage;
    }
}
