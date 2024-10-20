package com.koistorynew.ui.mymarket.model;

public class MarketImage {
    private String filePath;

    // Constructor
    public MarketImage(String filePath) {
        this.filePath = filePath;
    }

    // Getter cho filePath
    public String getFilePath() {
        return filePath;
    }

    // Setter cho filePath nếu cần
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
