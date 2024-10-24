package com.koistorynew.ui.mymarket.model;

public class MarketImage {
    private String filePath;
    private String id;

    public MarketImage(String filePath, String id) {
        this.filePath = filePath;
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getId() {
        return id;
    }
}
