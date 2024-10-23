package com.koistorynew.ui.fish.model;

public class KoiFish {
    private int imageResource;
    private String name;

    public KoiFish(int imageResource, String name) {
        this.imageResource = imageResource;
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }
}
