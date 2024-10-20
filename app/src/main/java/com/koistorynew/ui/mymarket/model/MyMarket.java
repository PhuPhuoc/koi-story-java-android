package com.koistorynew.ui.mymarket.model;

public class MyMarket {
    private String artName;
    private String image;
    private double price;
    private String description;

    public MyMarket(String artName, String image, double price, String description) {
        this.artName = artName;
        this.image = image;
        this.price = price;
        this.description = description;
    }

    public String getArtName() {
        return artName;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}

