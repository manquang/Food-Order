package com.example.foodorder.Model;

public class Category {
    String imgUrl, name, fileName;

    public Category() {
    }

    public Category(String imgUrl, String name, String fileName) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.fileName = fileName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
