package com.koistorynew.ui.market.model;

public class PostMarket {

    private String artName;
    private double price;
    private String description;
    private String image;

    public PostMarket(String artName, String image, double price, String description) {
        this.artName = artName;
        this.image = image;
        this.price = price;
        this.description = description;
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
