package com.koistorynew.ui.market.model;

public class PostMarket {
    private String id;
    private String artName;
    private double price;
    private String description;
    private String image;

    // Constructor cần phải được điều chỉnh
    public PostMarket(String id, String artName, String image, double price, String description) {
        this.id = id;
        this.artName = artName;
        this.image = image;
        this.price = price;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getArtName() {
        return artName;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}
