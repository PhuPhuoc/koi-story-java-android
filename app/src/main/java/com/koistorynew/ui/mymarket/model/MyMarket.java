package com.koistorynew.ui.mymarket.model;

public class MyMarket {
    private String id;
    private String artName;
    private double price;
    private String description;
    private String image;

    public MyMarket(String id, String artName, String image, double price, String description) {
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

